package com.fish_dan_.data_energistics.menu.universal;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGrid;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageHelper;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.encoding.PatternEncodingLogic;
import appeng.util.ConfigInventory;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.fish_dan_.data_energistics.menu.common.PatternProviderSyncHelper;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingSourceAware;
import com.fish_dan_.data_energistics.network.UniversalTerminalCyclePayload;
import com.fish_dan_.data_energistics.part.UniversalTerminalPart;
import com.fish_dan_.data_energistics.registry.ModMenus;
import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import net.neoforged.neoforge.network.PacketDistributor;

public class UniversalPatternEncodingTermMenu extends PatternEncodingTermMenu
        implements UniversalTerminalMenuBridge, PatternEncodingPreviewMenu {
    private static final String ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER = "transferEncodedPatternToProvider";
    private static final Field FALLBACK_NETWORK_BLANK_PATTERN_COUNT_FIELD =
            resolveInheritedField("dataEnergistics$networkBlankPatternCount");
    private static final Field FALLBACK_SYNCED_PATTERN_PROVIDERS_FIELD =
            resolveInheritedField("dataEnergistics$syncedPatternProviders");
    private static final int CRAFTING_GRID_WIDTH = 3;
    private static final int CRAFTING_GRID_HEIGHT = 3;
    private static final int CRAFTING_GRID_SLOTS = CRAFTING_GRID_WIDTH * CRAFTING_GRID_HEIGHT;
    private static final int PATTERN_PROVIDER_SYNC_INTERVAL_TICKS = 5;

    private final UniversalTerminalPart host;
    @GuiSync(890)
    public int availableTerminalMask;
    @GuiSync(891)
    public int activeTerminalIndex = -1;
    @GuiSync(892)
    public long networkBlankPatternCount;
    @GuiSync(893)
    public SyncedPatternProviderList syncedPatternProviders = SyncedPatternProviderList.EMPTY;

    private final Map<appeng.helpers.patternprovider.PatternContainer, Long> syncedPatternProviderIds = new IdentityHashMap<>();
    private long nextSyncedPatternProviderId = 1;
    private int lastPatternProviderSyncTick = Integer.MIN_VALUE;

    public UniversalPatternEncodingTermMenu(int id, Inventory playerInventory, UniversalTerminalPart host) {
        this(ModMenus.UNIVERSAL_PATTERN_ENCODING_TERM.get(), id, playerInventory, host, true);
    }

    public UniversalPatternEncodingTermMenu(MenuType<?> menuType, int id, Inventory playerInventory,
                                            UniversalTerminalPart host, boolean bindInventory) {
        super(menuType, id, playerInventory, host, bindInventory);
        this.host = host;
        registerClientAction(ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER, Long.class,
                this::transferEncodedPatternToProviderFromClient);
        syncTerminalState();
        syncBlankPatternCountFromNetwork();
        syncPatternProvidersIfNeeded(true);
    }

    @Override
    public void broadcastChanges() {
        if (this.isServerSide()) {
            syncTerminalState();
            syncBlankPatternCountFromNetwork();
            syncPatternProvidersIfNeeded(false);
        }
        super.broadcastChanges();
    }

    @Override
    public void encode() {
        if (isClientSide()) {
            sendClientAction("encode");
            return;
        }

        ItemStack encodedPattern = encodePatternVirtual();
        if (encodedPattern == null) {
            clearEncodedPatternSlot();
            return;
        }

        var encodedPatternInv = this.host.getLogic().getEncodedPatternInv();
        ItemStack encodeOutput = encodedPatternInv.getStackInSlot(0);
        if (!encodeOutput.isEmpty()
                && !PatternDetailsHelper.isEncodedPattern(encodeOutput)
                && !AEItems.BLANK_PATTERN.is(encodeOutput)) {
            return;
        }

        if (encodeOutput.isEmpty() && !consumeOneBlankPattern()) {
            return;
        }

        if (this instanceof PatternEncodingSourceAware sourceAware) {
            PatternEncodingSourceHelper.applyPatternSource(encodedPattern, sourceAware,
                    PatternEncodingSourceHelper.resolveFallbackWorkstationForMode(this.mode));
        }
        encodedPatternInv.setItemDirect(0, encodedPattern);
    }

    @Override
    public int getAvailableTerminalMask() {
        return this.availableTerminalMask;
    }

    @Override
    public int getActiveTerminalIndex() {
        return this.activeTerminalIndex;
    }

    @Override
    public UniversalTerminalPart getUniversalTerminalHost() {
        return this.host;
    }

    @Override
    public long getNetworkBlankPatternCount() {
        if (this.networkBlankPatternCount > 0) {
            return this.networkBlankPatternCount;
        }

        Long fallbackCount = readFallbackNetworkBlankPatternCount();
        return fallbackCount != null ? fallbackCount : this.networkBlankPatternCount;
    }

    @Override
    public List<SyncedPatternProvider> getSyncedPatternProviders() {
        if (!this.syncedPatternProviders.providers().isEmpty()) {
            return this.syncedPatternProviders.providers();
        }

        SyncedPatternProviderList fallbackProviders = readFallbackSyncedPatternProviders();
        return fallbackProviders != null ? fallbackProviders.providers() : this.syncedPatternProviders.providers();
    }

    @Override
    public void transferEncodedPatternToProvider(long providerId) {
        if (this.isClientSide()) {
            sendClientAction(ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER, providerId);
            return;
        }

        var provider = PatternProviderSyncHelper.findProviderById(this.syncedPatternProviderIds, providerId);
        if (provider == null) {
            syncPatternProvidersFromNetwork();
            provider = PatternProviderSyncHelper.findProviderById(this.syncedPatternProviderIds, providerId);
            if (provider == null) {
                return;
            }
        }

        var encodedPatternInv = this.host.getLogic().getEncodedPatternInv();
        ItemStack encodedPattern = encodedPatternInv.getStackInSlot(0);
        ItemStack remainder = PatternProviderSyncHelper.transferEncodedPatternToProvider(provider, encodedPattern);
        if (remainder.getCount() == encodedPattern.getCount()) {
            return;
        }

        encodedPatternInv.setItemDirect(0, remainder.isEmpty() ? ItemStack.EMPTY : remainder);
        syncPatternProvidersFromNetwork();
    }

    @Override
    public void sendCycleTerminal(boolean reverse) {
        PacketDistributor.sendToServer(new UniversalTerminalCyclePayload(reverse));
    }

    @Override
    public boolean isValidForSlot(Slot slot, ItemStack stack) {
        if (this.getSlotSemantic(slot) == SlotSemantics.BLANK_PATTERN) {
            return false;
        }
        return super.isValidForSlot(slot, stack);
    }

    private void syncTerminalState() {
        this.availableTerminalMask = this.host.getInstalledTerminalMask();
        this.activeTerminalIndex = this.host.getActiveTerminalIndex();
    }

    private void syncBlankPatternCountFromNetwork() {
        this.networkBlankPatternCount = 0;
        if (getActiveGrid() == null) {
            return;
        }

        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return;
        }

        this.networkBlankPatternCount = this.storage.getAvailableStacks().get(blankPatternKey);
    }

    private void syncPatternProvidersFromNetwork() {
        var grid = getActiveGrid();
        if (grid == null) {
            clearSyncedPatternProviders();
            return;
        }

        this.syncedPatternProviders = PatternProviderSyncHelper.collectSyncedPatternProviders(
                grid,
                this.syncedPatternProviderIds,
                () -> this.nextSyncedPatternProviderId++,
                this.host.getLogic().getEncodedPatternInv().getStackInSlot(0));
    }

    private void syncPatternProvidersIfNeeded(boolean force) {
        if (getActiveGrid() == null) {
            clearSyncedPatternProviders();
            this.lastPatternProviderSyncTick = Integer.MIN_VALUE;
            return;
        }

        int currentTick = this.getPlayer().tickCount;
        if (!force && currentTick - this.lastPatternProviderSyncTick < PATTERN_PROVIDER_SYNC_INTERVAL_TICKS) {
            return;
        }

        syncPatternProvidersFromNetwork();
        this.lastPatternProviderSyncTick = currentTick;
    }

    private void clearSyncedPatternProviders() {
        this.syncedPatternProviderIds.clear();
        this.syncedPatternProviders = SyncedPatternProviderList.EMPTY;
        this.lastPatternProviderSyncTick = Integer.MIN_VALUE;
    }

    @Nullable
    private IGrid getActiveGrid() {
        var gridNode = this.getGridNode();
        if (gridNode != null && gridNode.isActive()) {
            return gridNode.getGrid();
        }
        return null;
    }

    private boolean consumeOneBlankPattern() {
        var blankPatternInv = this.host.getLogic().getBlankPatternInv();
        ItemStack localBlankPattern = blankPatternInv.getStackInSlot(0);
        if (AEItems.BLANK_PATTERN.is(localBlankPattern) && localBlankPattern.getCount() > 0) {
            ItemStack reduced = localBlankPattern.copy();
            reduced.shrink(1);
            blankPatternInv.setItemDirect(0, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
            return true;
        }

        if (!this.canInteractWithGrid()) {
            return false;
        }

        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        return blankPatternKey != null
                && StorageHelper.poweredExtraction(
                        this.energySource,
                        this.storage,
                        blankPatternKey,
                        1,
                        this.getActionSource()) > 0;
    }

    private void transferEncodedPatternToProviderFromClient(Long providerId) {
        if (providerId != null) {
            transferEncodedPatternToProvider(providerId);
        }
    }

    @Nullable
    private static Field resolveInheritedField(String fieldName) {
        try {
            Field field = PatternEncodingTermMenu.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

    @Nullable
    private Long readFallbackNetworkBlankPatternCount() {
        if (FALLBACK_NETWORK_BLANK_PATTERN_COUNT_FIELD == null) {
            return null;
        }

        try {
            Object value = FALLBACK_NETWORK_BLANK_PATTERN_COUNT_FIELD.get(this);
            return value instanceof Long count ? count : null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    @Nullable
    private SyncedPatternProviderList readFallbackSyncedPatternProviders() {
        if (FALLBACK_SYNCED_PATTERN_PROVIDERS_FIELD == null) {
            return null;
        }

        try {
            Object value = FALLBACK_SYNCED_PATTERN_PROVIDERS_FIELD.get(this);
            return value instanceof SyncedPatternProviderList providers ? providers : null;
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private void clearEncodedPatternSlot() {
        var encodedPatternInv = this.host.getLogic().getEncodedPatternInv();
        ItemStack encodedPattern = encodedPatternInv.getStackInSlot(0);
        if (PatternDetailsHelper.isEncodedPattern(encodedPattern)) {
            encodedPatternInv.setItemDirect(0, AEItems.BLANK_PATTERN.stack(encodedPattern.getCount()));
        }
    }

    @Nullable
    private ItemStack encodePatternVirtual() {
        return switch (this.mode) {
            case CRAFTING -> encodeCraftingPatternVirtual();
            case PROCESSING -> encodeProcessingPatternVirtual();
            case SMITHING_TABLE -> encodeSmithingTablePatternVirtual();
            case STONECUTTING -> encodeStonecuttingPatternVirtual();
        };
    }

    @Nullable
    private ItemStack encodeCraftingPatternVirtual() {
        var ingredients = new ItemStack[CRAFTING_GRID_SLOTS];
        boolean valid = false;
        for (int x = 0; x < ingredients.length; x++) {
            ingredients[x] = getEncodedCraftingIngredient(x);
            if (ingredients[x] == null) {
                return null;
            } else if (!ingredients[x].isEmpty()) {
                valid = true;
            }
        }
        if (!valid) {
            return null;
        }

        var recipe = resolveCurrentCraftingRecipe(ingredients);
        if (recipe == null) {
            return null;
        }

        var level = this.getPlayer().level();
        var items = NonNullList.withSize(CRAFTING_GRID_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < ingredients.length; i++) {
            items.set(i, ingredients[i]);
        }
        var input = CraftingInput.of(CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT, items);
        var result = recipe.value().assemble(input, level.registryAccess());
        if (result.isEmpty()) {
            return null;
        }

        return PatternDetailsHelper.encodeCraftingPattern(recipe, ingredients, result, this.isSubstitute(),
                this.isSubstituteFluids());
    }

    @Nullable
    private RecipeHolder<CraftingRecipe> resolveCurrentCraftingRecipe(ItemStack[] ingredients) {
        var level = this.getPlayer().level();
        var items = NonNullList.withSize(CRAFTING_GRID_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < ingredients.length; i++) {
            items.set(i, ingredients[i] == null ? ItemStack.EMPTY : ingredients[i]);
        }
        var input = CraftingInput.of(CRAFTING_GRID_WIDTH, CRAFTING_GRID_HEIGHT, items);
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
    }

    @Nullable
    private ItemStack encodeProcessingPatternVirtual() {
        ConfigInventory encodedInputsInv = this.host.getLogic().getEncodedInputInv();
        ConfigInventory encodedOutputsInv = this.host.getLogic().getEncodedOutputInv();

        var inputs = new GenericStack[encodedInputsInv.size()];
        boolean valid = false;
        for (int slot = 0; slot < encodedInputsInv.size(); slot++) {
            inputs[slot] = encodedInputsInv.getStack(slot);
            if (inputs[slot] != null) {
                valid = true;
            }
        }
        if (!valid) {
            return null;
        }

        var outputs = new GenericStack[encodedOutputsInv.size()];
        for (int slot = 0; slot < encodedOutputsInv.size(); slot++) {
            outputs[slot] = encodedOutputsInv.getStack(slot);
        }
        if (outputs[0] == null) {
            return null;
        }

        return PatternDetailsHelper.encodeProcessingPattern(Arrays.asList(inputs), Arrays.asList(outputs));
    }

    @Nullable
    private ItemStack encodeSmithingTablePatternVirtual() {
        PatternEncodingLogic logic = this.host.getLogic();
        ConfigInventory encodedInputsInv = logic.getEncodedInputInv();

        if (!(encodedInputsInv.getKey(0) instanceof AEItemKey template)
                || !(encodedInputsInv.getKey(1) instanceof AEItemKey base)
                || !(encodedInputsInv.getKey(2) instanceof AEItemKey addition)) {
            return null;
        }

        var input = new SmithingRecipeInput(template.toStack(), base.toStack(), addition.toStack());
        var level = this.getPlayer().level();
        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, input, level).orElse(null);
        if (recipe == null) {
            return null;
        }

        var output = AEItemKey.of(recipe.value().assemble(input, level.registryAccess()));
        return PatternDetailsHelper.encodeSmithingTablePattern(recipe, template, base, addition, output,
                logic.isSubstitution());
    }

    @Nullable
    private ItemStack encodeStonecuttingPatternVirtual() {
        if (this.stonecuttingRecipeId == null) {
            return null;
        }

        ConfigInventory encodedInputsInv = this.host.getLogic().getEncodedInputInv();
        if (!(encodedInputsInv.getKey(0) instanceof AEItemKey input)) {
            return null;
        }

        var recipeInput = new SingleRecipeInput(input.toStack());
        var level = this.getPlayer().level();
        var recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.STONECUTTING, recipeInput, level, this.stonecuttingRecipeId)
                .orElse(null);
        if (recipe == null) {
            return null;
        }

        var output = AEItemKey.of(recipe.value().getResultItem(level.registryAccess()));
        return PatternDetailsHelper.encodeStonecuttingPattern(recipe, input, output,
                this.host.getLogic().isSubstitution());
    }

    @Nullable
    private ItemStack getEncodedCraftingIngredient(int slot) {
        var what = this.host.getLogic().getEncodedInputInv().getKey(slot);
        if (what == null) {
            return ItemStack.EMPTY;
        } else if (what instanceof AEItemKey itemKey) {
            return itemKey.toStack(1);
        } else {
            return null;
        }
    }

}
