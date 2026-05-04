package com.fish_dan_.data_energistics.mixin;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.storage.StorageHelper;
import appeng.api.storage.ITerminalHost;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEItems;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.fish_dan_.data_energistics.menu.common.PatternProviderSyncHelper;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingSourceAware;
import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
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
        implements PatternEncodingPreviewMenu, PatternEncodingSourceAware {
    @Unique
    private static final String DATA_ENERGISTICS_ACTION_TRANSFER_ENCODED_PATTERN_TO_PROVIDER =
            "dataEnergistics$transferEncodedPatternToProvider";
    @Unique
    private static final int DATA_ENERGISTICS_PATTERN_PROVIDER_SYNC_INTERVAL_TICKS = 5;

    @GuiSync(790)
    @Unique
    public long dataEnergistics$networkBlankPatternCount;

    @GuiSync(791)
    @Unique
    public SyncedPatternProviderList dataEnergistics$syncedPatternProviders = SyncedPatternProviderList.EMPTY;

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

    @Shadow
    @Final
    private RestrictedInputSlot blankPatternSlot;

    @Shadow
    @Final
    private RestrictedInputSlot encodedPatternSlot;

    protected PatternEncodingTermMenuMixin(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host,
                                           boolean bindInventory) {
        super(menuType, id, ip, host, bindInventory);
    }

    @Invoker("encodePattern")
    protected abstract ItemStack dataEnergistics$invokeEncodePattern();

    @Invoker("clearPattern")
    protected abstract void dataEnergistics$invokeClearPattern();

    @Override
    public long getNetworkBlankPatternCount() {
        return this.dataEnergistics$networkBlankPatternCount;
    }

    @Override
    public List<SyncedPatternProvider> getSyncedPatternProviders() {
        return this.dataEnergistics$syncedPatternProviders.providers();
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
    public void setPendingPatternSource(@Nullable ResourceLocation workstationId) {
        if (this.isClientSide()) {
            sendClientAction(PatternEncodingSourceHelper.ACTION_SET_PATTERN_SOURCE,
                    workstationId != null ? workstationId.toString() : PatternEncodingSourceHelper.CLEAR_PATTERN_SOURCE);
        } else {
            this.dataEnergistics$pendingPatternSource = workstationId;
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
        if (this.isServerSide()) {
            dataEnergistics$syncBlankPatternCountFromNetwork();
            dataEnergistics$syncPatternProvidersFromNetwork();
            this.dataEnergistics$lastPatternProviderSyncTick = this.getPlayer().tickCount;
        }
    }

    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void dataEnergistics$syncPreviewDataBeforeBroadcast(CallbackInfo ci) {
        if (this.isServerSide()) {
            dataEnergistics$syncBlankPatternCountFromNetwork();
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

    @Inject(method = "encode", at = @At("HEAD"), cancellable = true)
    private void dataEnergistics$encodeUsingNetworkBlankPatterns(CallbackInfo ci) {
        if (this.isClientSide()) {
            return;
        }

        ItemStack encodedPattern = dataEnergistics$invokeEncodePattern();
        if (encodedPattern == null) {
            dataEnergistics$invokeClearPattern();
            ci.cancel();
            return;
        }

        ItemStack encodeOutput = this.encodedPatternSlot.getItem();
        if (!encodeOutput.isEmpty()
                && !appeng.api.crafting.PatternDetailsHelper.isEncodedPattern(encodeOutput)
                && !AEItems.BLANK_PATTERN.is(encodeOutput)) {
            ci.cancel();
            return;
        }

        if (encodeOutput.isEmpty() && !dataEnergistics$consumeOneBlankPattern()) {
            ci.cancel();
            return;
        }

        PatternEncodingSourceHelper.applyPatternSource(encodedPattern, this,
                PatternEncodingSourceHelper.resolveFallbackWorkstationForMode(((PatternEncodingTermMenu) (Object) this).mode));
        this.encodedPatternSlot.set(encodedPattern);
        ci.cancel();
    }

    @Unique
    private void dataEnergistics$setPendingPatternSourceFromClient(String workstationId) {
        if (workstationId == null || workstationId.isEmpty()) {
            this.dataEnergistics$pendingPatternSource = null;
            return;
        }

        this.dataEnergistics$pendingPatternSource = ResourceLocation.tryParse(workstationId);
    }

    @Unique
    private void dataEnergistics$transferEncodedPatternToProviderFromClient(Long providerId) {
        if (providerId != null) {
            transferEncodedPatternToProvider(providerId);
        }
    }

    @Unique
    private void dataEnergistics$syncBlankPatternCountFromNetwork() {
        this.dataEnergistics$networkBlankPatternCount = 0;
        if (dataEnergistics$getActiveGrid() == null) {
            return;
        }

        var blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return;
        }

        this.dataEnergistics$networkBlankPatternCount = this.storage.getAvailableStacks().get(blankPatternKey);
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
    private boolean dataEnergistics$consumeOneBlankPattern() {
        ItemStack localBlankPattern = this.blankPatternSlot.getItem();
        if (AEItems.BLANK_PATTERN.is(localBlankPattern) && localBlankPattern.getCount() > 0) {
            localBlankPattern.shrink(1);
            if (localBlankPattern.isEmpty()) {
                this.blankPatternSlot.set(ItemStack.EMPTY);
            }
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

}
