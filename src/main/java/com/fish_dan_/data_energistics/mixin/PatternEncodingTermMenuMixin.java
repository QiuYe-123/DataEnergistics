package com.fish_dan_.data_energistics.mixin;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageHelper;
import appeng.api.storage.ITerminalHost;
import appeng.core.definitions.AEItems;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.parts.encoding.EncodingMode;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.fish_dan_.data_energistics.menu.common.BlankPatternProxyMenu;
import com.fish_dan_.data_energistics.menu.common.PatternProviderMenuOpenHelper;
import com.fish_dan_.data_energistics.menu.common.PatternProviderSyncHelper;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingSourceAware;
import com.fish_dan_.data_energistics.util.PatternProviderNameHelper;
import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuMixin extends MEStorageMenu
        implements PatternEncodingPreviewMenu, PatternEncodingSourceAware, BlankPatternProxyMenu {
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER =
            "dataEnergistics$transferEncodedPatternToProvider";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_OPEN_PATTERN_PROVIDER_MENU =
            "dataEnergistics$openPatternProviderMenu";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_RENAME_PATTERN_PROVIDER =
            "dataEnergistics$renamePatternProvider";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_SET_PATTERN_SOURCE_ENABLED =
            "dataEnergistics$setPatternSourceEnabled";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_CLEAR_PATTERN_SOURCE_STATE =
            "dataEnergistics$clearPatternSourceState";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_DEPOSIT_CARRIED_BLANK_PATTERNS =
            "dataEnergistics$depositCarriedBlankPatterns";
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_PICKUP_BLANK_PATTERNS =
            "dataEnergistics$pickupBlankPatterns";
    @Unique
    private static final int DATA_ENERGISTICS_PATTERN_PROVIDER_SYNC_INTERVAL_TICKS = 5;

    @GuiSync(791)
    @Unique
    public SyncedPatternProviderList dataEnergistics$syncedPatternProviders = SyncedPatternProviderList.EMPTY;

    @GuiSync(792)
    @Unique
    public boolean dataEnergistics$patternSourceEnabled = true;

    @Unique
    private final Map<PatternContainer, Long> dataEnergistics$syncedPatternProviderIds = new IdentityHashMap<>();
    @Unique
    private final Map<Long, List<PatternContainer>> dataEnergistics$syncedPatternProvidersById = new HashMap<>();

    @Unique
    private long dataEnergistics$nextSyncedPatternProviderId = 1;

    @Unique
    private int dataEnergistics$lastPatternProviderSyncTick = Integer.MIN_VALUE;
    @Unique
    @Nullable
    private ResourceLocation dataEnergistics$pendingPatternSource;
    @GuiSync(793)
    @Unique
    @Nullable
    private ResourceLocation dataEnergistics$lastEncodedPatternSource;

    @Shadow
    @Final
    private RestrictedInputSlot blankPatternSlot;

    @Shadow
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    @Shadow
    public appeng.parts.encoding.EncodingMode mode;

    protected PatternEncodingTermMenuMixin(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host,
                                           boolean bindInventory) {
        super(menuType, id, ip, host, bindInventory);
    }

    @Invoker("encodePattern")
    protected abstract ItemStack dataEnergistics$invokeEncodePattern();

    @Invoker("clearPattern")
    protected abstract void dataEnergistics$invokeClearPattern();

    @Override
    public List<SyncedPatternProvider> getSyncedPatternProviders() {
        return this.dataEnergistics$syncedPatternProviders.providers();
    }

    @Override
    public EncodingMode getEncodingMode() {
        return this.mode;
    }

    @Override
    public long getNetworkBlankPatternCount() {
        if (!this.canInteractWithGrid()) {
            return 0;
        }

        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return 0;
        }

        return this.storage.getAvailableStacks().get(blankPatternKey);
    }

    @Override
    public void depositCarriedBlankPatterns(boolean single) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_DEPOSIT_CARRIED_BLANK_PATTERNS, single);
            return;
        }

        ItemStack carried = this.getCarried();
        if (!AEItems.BLANK_PATTERN.is(carried) || carried.isEmpty() || !this.canInteractWithGrid()) {
            return;
        }

        int amountToInsert = single ? 1 : carried.getCount();
        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return;
        }

        long inserted = StorageHelper.poweredInsert(
                this.energySource,
                this.storage,
                blankPatternKey,
                amountToInsert,
                this.getActionSource(),
                Actionable.MODULATE);
        if (inserted <= 0) {
            return;
        }

        ItemStack updated = carried.copy();
        updated.shrink((int) inserted);
        this.setCarried(updated.isEmpty() ? ItemStack.EMPTY : updated);
    }

    @Override
    public void pickupBlankPatterns(boolean single) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_PICKUP_BLANK_PATTERNS, single);
            return;
        }

        ItemStack carried = this.getCarried();
        if (!carried.isEmpty() && !AEItems.BLANK_PATTERN.is(carried)) {
            return;
        }

        int maxStackSize = AEItems.BLANK_PATTERN.stack().getMaxStackSize();
        int currentCount = carried.isEmpty() ? 0 : carried.getCount();
        int remainingToPickup = single ? 1 : maxStackSize - currentCount;
        if (remainingToPickup <= 0) {
            return;
        }

        ItemStack updated = carried.isEmpty() ? ItemStack.EMPTY : carried.copy();

        if (remainingToPickup > 0 && this.canInteractWithGrid()) {
            var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
            if (blankPatternKey != null) {
                long extracted = StorageHelper.poweredExtraction(
                        this.energySource,
                        this.storage,
                        blankPatternKey,
                        remainingToPickup,
                        this.getActionSource(),
                        Actionable.MODULATE);
                if (extracted > 0) {
                    if (updated.isEmpty()) {
                        updated = AEItems.BLANK_PATTERN.stack((int) extracted);
                    } else {
                        updated.grow((int) extracted);
                    }
                }
            }
        }

        if (!updated.isEmpty()) {
            this.setCarried(updated);
        }
    }

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private void dataEnergistics$encodeUsingNetworkBlankPatterns(CallbackInfo ci) {
        if (this.isClientSide()) {
            return;
        }

        ItemStack encodedPattern = this.dataEnergistics$invokeEncodePattern();
        if (encodedPattern == null) {
            this.dataEnergistics$invokeClearPattern();
            ci.cancel();
            return;
        }

        ItemStack encodeOutput = this.encodedPatternSlot.getItem();
        if (!encodeOutput.isEmpty()
                && !PatternDetailsHelper.isEncodedPattern(encodeOutput)
                && !AEItems.BLANK_PATTERN.is(encodeOutput)) {
            ci.cancel();
            return;
        }

        if (encodeOutput.isEmpty() && !dataEnergistics$consumeOneBlankPatternFromNetwork()) {
            ci.cancel();
            return;
        }

        this.encodedPatternSlot.set(encodedPattern);
        ci.cancel();
    }

    @Override
    public void transferEncodedPatternToProvider(long providerId) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER, providerId);
            return;
        }

        var providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
        if (providers == null || providers.isEmpty()) {
            dataEnergistics$syncPatternProvidersFromNetwork();
            providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
            if (providers == null || providers.isEmpty()) {
                return;
            }
        }

        ItemStack encodedPattern = this.encodedPatternSlot.getItem();
        ItemStack remainder = PatternProviderSyncHelper.transferEncodedPatternToProviders(providers, encodedPattern);
        if (remainder.getCount() == encodedPattern.getCount()) {
            return;
        }

        this.encodedPatternSlot.set(remainder.isEmpty() ? ItemStack.EMPTY : remainder);
        dataEnergistics$syncPatternProvidersFromNetwork();
    }

    @Override
    public void openPatternProviderMenu(long providerId) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_OPEN_PATTERN_PROVIDER_MENU, providerId);
            return;
        }

        var providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
        if (providers == null || providers.isEmpty()) {
            dataEnergistics$syncPatternProvidersFromNetwork();
            providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
            if (providers == null || providers.isEmpty()) {
                return;
            }
        }

        PatternProviderMenuOpenHelper.openProviderGroup(providers, this.getPlayer());
    }

    @Override
    public void renamePatternProvider(long providerId, String name) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_RENAME_PATTERN_PROVIDER,
                    providerId + "\n" + (name == null ? "" : name));
            return;
        }

        var providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
        if (providers == null || providers.isEmpty()) {
            dataEnergistics$syncPatternProvidersFromNetwork();
            providers = PatternProviderSyncHelper.findProvidersById(this.dataEnergistics$syncedPatternProvidersById, providerId);
            if (providers == null || providers.isEmpty()) {
                return;
            }
        }

        dataEnergistics$renamePatternProvider(providers.getFirst(), name);
    }

    @Override
    public void setPendingPatternSource(@Nullable ResourceLocation workstationId) {
        if (this.isClientSide()) {
            sendClientAction(PatternEncodingSourceHelper.ACTION_SET_PATTERN_SOURCE,
                    workstationId != null ? workstationId.toString() : PatternEncodingSourceHelper.CLEAR_PATTERN_SOURCE);
            this.dataEnergistics$pendingPatternSource = workstationId;
        } else {
            this.dataEnergistics$pendingPatternSource = workstationId;
            PatternEncodingSourceHelper.writePendingPatternSource(this.getPlayer(), workstationId);
            if (this.dataEnergistics$patternSourceEnabled) {
                setLastEncodedPatternSource(workstationId);
            }
        }
    }

    @Override
    public @Nullable ResourceLocation getPendingPatternSource() {
        return this.dataEnergistics$pendingPatternSource;
    }

    @Override
    public void clearPendingPatternSource() {
        this.dataEnergistics$pendingPatternSource = null;
    }

    @Override
    public void clearPatternSourceState() {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_CLEAR_PATTERN_SOURCE_STATE);
            this.dataEnergistics$pendingPatternSource = null;
            this.dataEnergistics$lastEncodedPatternSource = null;
            return;
        }

        this.dataEnergistics$pendingPatternSource = null;
        this.dataEnergistics$lastEncodedPatternSource = null;
        PatternEncodingSourceHelper.writePendingPatternSource(this.getPlayer(), null);
        PatternEncodingSourceHelper.writeLastEncodedPatternSource(this.getPlayer(), null);
    }

    @Override
    public @Nullable ResourceLocation getLastEncodedPatternSource() {
        return this.dataEnergistics$lastEncodedPatternSource;
    }

    @Override
    public void setLastEncodedPatternSource(@Nullable ResourceLocation workstationId) {
        this.dataEnergistics$lastEncodedPatternSource = workstationId;
        if (this.isServerSide()) {
            PatternEncodingSourceHelper.writeLastEncodedPatternSource(this.getPlayer(), workstationId);
        }
    }

    @Override
    public boolean isPatternSourceEnabled() {
        return this.dataEnergistics$patternSourceEnabled;
    }

    @Override
    public void setPatternSourceEnabled(boolean enabled) {
        if (this.isClientSide()) {
            sendClientAction(DATA_ENERGISTICS_ACTION_SET_PATTERN_SOURCE_ENABLED, enabled);
        }
        this.dataEnergistics$patternSourceEnabled = enabled;
        if (!enabled) {
            this.dataEnergistics$pendingPatternSource = null;
            this.dataEnergistics$lastEncodedPatternSource = null;
        }
        if (this.isServerSide()) {
            PatternEncodingSourceHelper.writePatternSourceEnabled(this.getPlayer(), enabled);
        }
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/IPatternTerminalMenuHost;Z)V",
            at = @At("RETURN"))
    private void dataEnergistics$registerPatternSourceAction(MenuType<?> menuType, int id, Inventory ip,
                                                             IPatternTerminalMenuHost host, boolean bindInventory,
                                                             CallbackInfo ci) {
        registerClientAction(PatternEncodingSourceHelper.ACTION_SET_PATTERN_SOURCE, String.class,
                this::dataEnergistics$setPendingPatternSourceFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER, Long.class,
                this::dataEnergistics$transferEncodedPatternToProviderFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_OPEN_PATTERN_PROVIDER_MENU, Long.class,
                this::dataEnergistics$openPatternProviderMenuFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_RENAME_PATTERN_PROVIDER, String.class,
                this::dataEnergistics$renamePatternProviderFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_SET_PATTERN_SOURCE_ENABLED, Boolean.class,
                this::dataEnergistics$setPatternSourceEnabledFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_CLEAR_PATTERN_SOURCE_STATE,
                this::clearPatternSourceState);
        registerClientAction(DATA_ENERGISTICS_ACTION_DEPOSIT_CARRIED_BLANK_PATTERNS, Boolean.class,
                this::dataEnergistics$depositCarriedBlankPatternsFromClient);
        registerClientAction(DATA_ENERGISTICS_ACTION_PICKUP_BLANK_PATTERNS, Boolean.class,
                this::dataEnergistics$pickupBlankPatternsFromClient);
        this.blankPatternSlot.setHideAmount(true);
        if (this.isServerSide()) {
            this.dataEnergistics$patternSourceEnabled =
                    PatternEncodingSourceHelper.readPatternSourceEnabled(this.getPlayer());
            this.dataEnergistics$pendingPatternSource =
                    PatternEncodingSourceHelper.readPendingPatternSource(this.getPlayer());
            this.dataEnergistics$lastEncodedPatternSource =
                    PatternEncodingSourceHelper.readLastEncodedPatternSource(this.getPlayer());
            dataEnergistics$flushBlankPatternSlotToNetwork();
            dataEnergistics$syncPatternProvidersFromNetwork();
            this.dataEnergistics$lastPatternProviderSyncTick = this.getPlayer().tickCount;
        }
    }

    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void dataEnergistics$syncPreviewDataBeforeBroadcast(CallbackInfo ci) {
        if (this.isServerSide()) {
            dataEnergistics$flushBlankPatternSlotToNetwork();
            dataEnergistics$syncPatternProvidersIfNeeded();
        }
    }

    @Inject(method = "setMode", at = @At("HEAD"))
    private void dataEnergistics$updatePendingPatternSourceOnModeChange(appeng.parts.encoding.EncodingMode mode,
                                                                        CallbackInfo ci) {
        var fallbackWorkstation = PatternEncodingSourceHelper.resolveFallbackWorkstationForMode(mode);
        if (fallbackWorkstation != null) {
            this.dataEnergistics$pendingPatternSource = fallbackWorkstation;
        }
    }

    @Unique
    private void dataEnergistics$setPendingPatternSourceFromClient(String workstationId) {
        setPendingPatternSource(workstationId == null || workstationId.isEmpty()
                ? null
                : ResourceLocation.tryParse(workstationId));
    }

    @Unique
    private void dataEnergistics$transferEncodedPatternToProviderFromClient(Long providerId) {
        if (providerId != null) {
            transferEncodedPatternToProvider(providerId);
        }
    }

    @Unique
    private void dataEnergistics$openPatternProviderMenuFromClient(Long providerId) {
        if (providerId != null) {
            openPatternProviderMenu(providerId);
        }
    }

    @Unique
    private void dataEnergistics$renamePatternProviderFromClient(String payload) {
        if (payload == null) {
            return;
        }
        int separator = payload.indexOf('\n');
        if (separator < 0) {
            return;
        }

        try {
            long providerId = Long.parseLong(payload.substring(0, separator));
            String name = payload.substring(separator + 1);
            renamePatternProvider(providerId, name);
        } catch (NumberFormatException ignored) {
        }
    }

    @Unique
    private void dataEnergistics$setPatternSourceEnabledFromClient(Boolean enabled) {
        if (enabled != null) {
            setPatternSourceEnabled(enabled);
        }
    }

    @Unique
    private void dataEnergistics$depositCarriedBlankPatternsFromClient(Boolean single) {
        depositCarriedBlankPatterns(Boolean.TRUE.equals(single));
    }

    @Unique
    private void dataEnergistics$pickupBlankPatternsFromClient(Boolean single) {
        pickupBlankPatterns(Boolean.TRUE.equals(single));
    }

    @Unique
    private void dataEnergistics$renamePatternProvider(PatternContainer provider, @Nullable String name) {
        if (!PatternProviderSyncHelper.isRenameableProvider(provider)) {
            return;
        }

        String sanitized = name == null ? "" : name.trim();
        Component customName = sanitized.isEmpty() ? null : Component.literal(sanitized);
        if (!PatternProviderNameHelper.setCustomName(provider, customName)) {
            return;
        }

        PatternProviderNameHelper.syncRename(provider);

        dataEnergistics$syncPatternProvidersFromNetwork();
    }

    @Unique
    private void dataEnergistics$syncPatternProvidersIfNeeded() {
        if (dataEnergistics$getActiveGrid() == null) {
            dataEnergistics$clearSyncedPatternProviders();
            return;
        }

        int currentTick = this.getPlayer().tickCount;
        if (currentTick - this.dataEnergistics$lastPatternProviderSyncTick
                < DATA_ENERGISTICS_PATTERN_PROVIDER_SYNC_INTERVAL_TICKS) {
            return;
        }

        dataEnergistics$syncPatternProvidersFromNetwork();
        this.dataEnergistics$lastPatternProviderSyncTick = currentTick;
    }

    @Unique
    private void dataEnergistics$syncPatternProvidersFromNetwork() {
        var grid = dataEnergistics$getActiveGrid();
        if (grid == null) {
            dataEnergistics$clearSyncedPatternProviders();
            return;
        }

        this.dataEnergistics$syncedPatternProviders = PatternProviderSyncHelper.collectSyncedPatternProviders(
                grid,
                this.dataEnergistics$syncedPatternProviderIds,
                this.dataEnergistics$syncedPatternProvidersById,
                () -> this.dataEnergistics$nextSyncedPatternProviderId++,
                PatternEncodingSourceHelper.resolvePreferredWorkstationId(this),
                this.mode,
                this.encodedPatternSlot.getItem());
    }

    @Unique
    private void dataEnergistics$clearSyncedPatternProviders() {
        this.dataEnergistics$syncedPatternProviderIds.clear();
        this.dataEnergistics$syncedPatternProvidersById.clear();
        this.dataEnergistics$syncedPatternProviders = SyncedPatternProviderList.EMPTY;
        this.dataEnergistics$lastPatternProviderSyncTick = Integer.MIN_VALUE;
    }

    @Unique
    private IGrid dataEnergistics$getActiveGrid() {
        IGridNode hostNode = dataEnergistics$tryResolveGridNode();
        if (hostNode != null && hostNode.isActive()) {
            return hostNode.getGrid();
        }
        return null;
    }

    @Unique
    @Nullable
    private IGridNode dataEnergistics$tryResolveGridNode() {
        try {
            IGridNode hostNode = this.getGridNode();
            if (hostNode != null) {
                return hostNode;
            }
        } catch (NullPointerException ignored) {
            // Wireless terminal menus initialize their actionable host after the base constructor runs.
        }

        try {
            if (this.getHost() instanceof IActionHost actionHost) {
                return actionHost.getActionableNode();
            }
        } catch (NullPointerException ignored) {
            // Host not fully initialized yet; broadcastChanges() will sync once construction finishes.
        }

        return null;
    }

    @Unique
    private boolean dataEnergistics$consumeOneBlankPatternFromNetwork() {
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
                this.getActionSource(),
                Actionable.MODULATE) > 0;
    }

    @Unique
    private void dataEnergistics$flushBlankPatternSlotToNetwork() {
        ItemStack slotStack = this.blankPatternSlot.getItem();
        if (!AEItems.BLANK_PATTERN.is(slotStack) || slotStack.isEmpty() || !this.canInteractWithGrid()) {
            return;
        }

        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return;
        }

        long inserted = StorageHelper.poweredInsert(
                this.energySource,
                this.storage,
                blankPatternKey,
                slotStack.getCount(),
                this.getActionSource(),
                Actionable.MODULATE);
        if (inserted <= 0) {
            return;
        }

        ItemStack reduced = slotStack.copy();
        reduced.shrink((int) inserted);
        this.blankPatternSlot.set(reduced.isEmpty() ? ItemStack.EMPTY : reduced);
    }

}
