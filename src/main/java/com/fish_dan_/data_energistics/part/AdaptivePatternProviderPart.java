package com.fish_dan_.data_energistics.part;

import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.parts.crafting.PatternProviderPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.fish_dan_.data_energistics.ae2.AdaptivePatternProviderHost;
import com.fish_dan_.data_energistics.ae2.AdaptivePatternProviderLogic;
import com.fish_dan_.data_energistics.blockentity.AdaptivePatternProviderBlockEntity;
import com.fish_dan_.data_energistics.registry.ModMenus;
import com.moakiee.ae2lt.blockentity.OverloadedPatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AdaptivePatternProviderPart extends PatternProviderPart implements InternalInventoryHost, IUpgradeableObject, AdaptivePatternProviderHost {
    private static final String PROVIDER_SLOT_TAG = "provider_slot";
    private static final String UPGRADES_TAG = "upgrades";
    private static final String ADVANCED_AE_FILTERED_IMPORT_TAG = "advanced_ae_filtered_import";
    private static final String AE2LT_PROVIDER_MODE_TAG = "ae2lt_provider_mode";
    private static final String AE2LT_RETURN_MODE_TAG = "ae2lt_return_mode";
    private static final String AE2LT_WIRELESS_DISPATCH_MODE_TAG = "ae2lt_wireless_dispatch_mode";
    private static final String AE2LT_WIRELESS_SPEED_MODE_TAG = "ae2lt_wireless_speed_mode";
    private static final String AE2LT_CONNECTIONS_TAG = "ae2lt_wireless_connections";
    private static final int PROVIDER_SLOT_LIMIT = 4;
    private static final int EXTRA_PROVIDER_SLOTS_PER_CAPACITY_CARD = 4;
    private static final int APPFLUX_UPGRADE_SLOTS = 6;
    private static final int MAX_CAPACITY_CARD_UPGRADES = 3;
    private static final int MAX_PROVIDER_SLOT_LIMIT =
            PROVIDER_SLOT_LIMIT + MAX_CAPACITY_CARD_UPGRADES * EXTRA_PROVIDER_SLOTS_PER_CAPACITY_CARD;
    private static final int MAX_NETWORK_SAFE_MENU_SLOTS = Short.MAX_VALUE + 1;
    private static final int FIXED_MENU_SLOT_OVERHEAD =
            36 + 18 + 1 + 36 + (APPFLUX_UPGRADE_SLOTS * 2);
    private static final int MAX_PATTERN_SLOTS = MAX_NETWORK_SAFE_MENU_SLOTS - FIXED_MENU_SLOT_OVERHEAD;

    private final AppEngInternalInventory providerInventory = new AppEngInternalInventory(this, 1);
    private final IUpgradeInventory upgrades;
    private final List<OverloadedPatternProviderBlockEntity.WirelessConnection> ae2LtConnections = new ArrayList<>();
    private boolean advancedAeFilteredImport;
    private AdaptivePatternProviderBlockEntity.Ae2LtProviderMode ae2LtProviderMode =
            AdaptivePatternProviderBlockEntity.Ae2LtProviderMode.NORMAL;
    private AdaptivePatternProviderBlockEntity.Ae2LtReturnMode ae2LtReturnMode =
            AdaptivePatternProviderBlockEntity.Ae2LtReturnMode.OFF;
    private AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode ae2LtWirelessDispatchMode =
            AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode.EVEN_DISTRIBUTION;
    private AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode ae2LtWirelessSpeedMode =
            AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode.NORMAL;

    public AdaptivePatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
        this.upgrades = createUpgradeInventory();
        this.providerInventory.setMaxStackSize(0, getProviderSlotLimit());
        this.providerInventory.setFilter(new ProviderSuffixFilter());
    }

    @Override
    protected AdaptivePatternProviderLogic createLogic() {
        return new AdaptivePatternProviderLogic(this.getMainNode(), this, MAX_PATTERN_SLOTS);
    }

    @Override
    public AdaptivePatternProviderLogic getLogic() {
        return (AdaptivePatternProviderLogic) super.getLogic();
    }

    @Override
    public AppEngInternalInventory getProviderInventory() {
        return this.providerInventory;
    }

    @Override
    public int getProviderSlotLimit() {
        return PROVIDER_SLOT_LIMIT + getExtraProviderSlotsFromCapacityCards();
    }

    @Override
    public ItemStack extractProviderOverflow() {
        if (this.providerInventory == null) {
            return ItemStack.EMPTY;
        }
        this.providerInventory.setMaxStackSize(0, getProviderSlotLimit());
        ItemStack providerStack = getProviderStack();
        if (providerStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int providerLimit = getProviderSlotLimit();
        if (providerStack.getCount() <= providerLimit) {
            return ItemStack.EMPTY;
        }

        int overflowCount = providerStack.getCount() - providerLimit;
        ItemStack keptStack = providerStack.copyWithCount(providerLimit);
        ItemStack overflowStack = providerStack.copyWithCount(overflowCount);
        this.providerInventory.setItemDirect(0, keptStack);
        return overflowStack;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public int getPatternSlotCountForMenu() {
        return getConfiguredPatternSlotCount();
    }

    @Override
    public Component getProviderDisplayName() {
        var adjacentGroup = getAdjacentMachineGroup();
        if (adjacentGroup != null) {
            return adjacentGroup.name();
        }

        ItemStack providerStack = getProviderStack();
        Component displayName = AdaptivePatternProviderBlockEntity.getResolvedProviderDisplayName(providerStack);
        return displayName != null ? displayName : this.getMainMenuIcon().getHoverName();
    }

    @Override
    public Component getGuiDisplayName() {
        var adjacentGroup = getAdjacentMachineGroup();
        if (adjacentGroup != null) {
            return AdaptivePatternProviderBlockEntity.decorateAttachedMachineName(
                    adjacentGroup.name(),
                    getResolvedProviderNameForGui()
            );
        }

        ItemStack providerStack = getProviderStack();
        Component displayName = AdaptivePatternProviderBlockEntity.getResolvedProviderDisplayName(providerStack);
        return displayName != null ? displayName : this.getMainMenuIcon().getHoverName();
    }

    @Override
    public boolean isMeteoriteProviderSelected() {
        return AdaptivePatternProviderBlockEntity.getResolvedProviderKind(getProviderStack())
                == AdaptivePatternProviderBlockEntity.ProviderKind.METEORITE;
    }

    @Override
    public boolean isAdvancedAeProviderSelected() {
        var kind = AdaptivePatternProviderBlockEntity.getResolvedProviderKind(getProviderStack());
        return kind == AdaptivePatternProviderBlockEntity.ProviderKind.ADVANCED_SMALL
                || kind == AdaptivePatternProviderBlockEntity.ProviderKind.ADVANCED_EXTENDED;
    }

    @Override
    public boolean isAe2LightningTechOverloadedProviderSelected() {
        return AdaptivePatternProviderBlockEntity.getResolvedProviderKind(getProviderStack())
                == AdaptivePatternProviderBlockEntity.ProviderKind.AE2LT_OVERLOADED;
    }

    @Override
    public boolean supportsFilteredImportToggle() {
        var kind = AdaptivePatternProviderBlockEntity.getResolvedProviderKind(getProviderStack());
        return kind == AdaptivePatternProviderBlockEntity.ProviderKind.ADVANCED_SMALL
                || kind == AdaptivePatternProviderBlockEntity.ProviderKind.ADVANCED_EXTENDED
                || kind == AdaptivePatternProviderBlockEntity.ProviderKind.AE2LT_OVERLOADED;
    }

    @Override
    public AdaptivePatternProviderBlockEntity.Ae2LtProviderMode getAe2LtProviderMode() {
        return this.ae2LtProviderMode;
    }

    @Override
    public void cycleAe2LtProviderMode() {
        this.ae2LtProviderMode = this.ae2LtProviderMode.next();
        onAdaptiveStateChanged();
    }

    @Override
    public boolean isAe2LtWirelessMode() {
        return this.ae2LtProviderMode == AdaptivePatternProviderBlockEntity.Ae2LtProviderMode.WIRELESS;
    }

    @Override
    public AdaptivePatternProviderBlockEntity.Ae2LtReturnMode getAe2LtReturnMode() {
        return this.ae2LtReturnMode;
    }

    @Override
    public void cycleAe2LtReturnMode() {
        this.ae2LtReturnMode = this.ae2LtReturnMode.next();
        onAdaptiveStateChanged();
    }

    @Override
    public AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode getAe2LtWirelessDispatchMode() {
        return this.ae2LtWirelessDispatchMode;
    }

    @Override
    public void cycleAe2LtWirelessDispatchMode() {
        this.ae2LtWirelessDispatchMode = this.ae2LtWirelessDispatchMode.next();
        onAdaptiveStateChanged();
    }

    @Override
    public AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode getAe2LtWirelessSpeedMode() {
        return this.ae2LtWirelessSpeedMode;
    }

    @Override
    public void cycleAe2LtWirelessSpeedMode() {
        this.ae2LtWirelessSpeedMode = this.ae2LtWirelessSpeedMode.next();
        onAdaptiveStateChanged();
    }

    @Override
    public boolean isAdvancedAeFilteredImportEnabled() {
        return this.advancedAeFilteredImport;
    }

    @Override
    public void setAdvancedAeFilteredImportEnabled(boolean enabled) {
        if (this.advancedAeFilteredImport == enabled) {
            return;
        }

        this.advancedAeFilteredImport = enabled;
        onAdaptiveStateChanged();
    }

    @Override
    public void addOrUpdateConnection(ResourceKey<Level> dimension, BlockPos pos, Direction boundFace) {
        for (int i = 0; i < this.ae2LtConnections.size(); i++) {
            var connection = this.ae2LtConnections.get(i);
            if (connection.sameTarget(dimension, pos)) {
                this.ae2LtConnections.set(i, new OverloadedPatternProviderBlockEntity.WirelessConnection(dimension, pos, boundFace));
                onAdaptiveStateChanged();
                return;
            }
        }

        this.ae2LtConnections.add(new OverloadedPatternProviderBlockEntity.WirelessConnection(dimension, pos, boundFace));
        onAdaptiveStateChanged();
    }

    @Override
    public boolean removeConnection(ResourceKey<Level> dimension, BlockPos pos) {
        Iterator<OverloadedPatternProviderBlockEntity.WirelessConnection> iterator = this.ae2LtConnections.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().sameTarget(dimension, pos)) {
                iterator.remove();
                onAdaptiveStateChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<OverloadedPatternProviderBlockEntity.WirelessConnection> getConnections() {
        return Collections.unmodifiableList(this.ae2LtConnections);
    }

    @Override
    public void markForClientUpdate() {
        if (this.getHost() != null) {
            this.getHost().markForUpdate();
        }
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(ModMenus.ADAPTIVE_PATTERN_PROVIDER.get(), player, MenuLocators.forPart(this));
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ModMenus.ADAPTIVE_PATTERN_PROVIDER.get(), player, subMenu.getLocator());
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.providerInventory.readFromNBT(data, PROVIDER_SLOT_TAG, registries);
        this.upgrades.readFromNBT(data, UPGRADES_TAG, registries);
        this.providerInventory.setMaxStackSize(0, getProviderSlotLimit());
        this.advancedAeFilteredImport = data.getBoolean(ADVANCED_AE_FILTERED_IMPORT_TAG);
        this.ae2LtProviderMode = readEnum(data, AE2LT_PROVIDER_MODE_TAG,
                AdaptivePatternProviderBlockEntity.Ae2LtProviderMode.NORMAL,
                AdaptivePatternProviderBlockEntity.Ae2LtProviderMode.class);
        this.ae2LtReturnMode = readEnum(data, AE2LT_RETURN_MODE_TAG,
                AdaptivePatternProviderBlockEntity.Ae2LtReturnMode.OFF,
                AdaptivePatternProviderBlockEntity.Ae2LtReturnMode.class);
        this.ae2LtWirelessDispatchMode = readEnum(data, AE2LT_WIRELESS_DISPATCH_MODE_TAG,
                AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode.EVEN_DISTRIBUTION,
                AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode.class);
        this.ae2LtWirelessSpeedMode = readEnum(data, AE2LT_WIRELESS_SPEED_MODE_TAG,
                AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode.NORMAL,
                AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode.class);
        this.ae2LtConnections.clear();
        ListTag connectionList = data.getList(AE2LT_CONNECTIONS_TAG, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < connectionList.size(); i++) {
            this.ae2LtConnections.add(OverloadedPatternProviderBlockEntity.WirelessConnection.fromTag(connectionList.getCompound(i)));
        }
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.providerInventory.writeToNBT(data, PROVIDER_SLOT_TAG, registries);
        this.upgrades.writeToNBT(data, UPGRADES_TAG, registries);
        data.putBoolean(ADVANCED_AE_FILTERED_IMPORT_TAG, this.advancedAeFilteredImport);
        data.putString(AE2LT_PROVIDER_MODE_TAG, this.ae2LtProviderMode.name());
        data.putString(AE2LT_RETURN_MODE_TAG, this.ae2LtReturnMode.name());
        data.putString(AE2LT_WIRELESS_DISPATCH_MODE_TAG, this.ae2LtWirelessDispatchMode.name());
        data.putString(AE2LT_WIRELESS_SPEED_MODE_TAG, this.ae2LtWirelessSpeedMode.name());
        ListTag connectionList = new ListTag();
        for (var connection : this.ae2LtConnections) {
            connectionList.add(connection.toTag());
        }
        data.put(AE2LT_CONNECTIONS_TAG, connectionList);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        ItemStack stack = getProviderStack();
        if (!stack.isEmpty()) {
            drops.add(stack.copy());
        }
        if (this.upgrades != null) {
            for (ItemStack upgrade : this.upgrades) {
                if (!upgrade.isEmpty()) {
                    drops.add(upgrade.copy());
                }
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        if (this.providerInventory != null) {
            this.providerInventory.clear();
        }
        if (this.upgrades != null) {
            this.upgrades.clear();
        }
        this.ae2LtConnections.clear();
    }

    @Override
    public AEItemKey getTerminalIcon() {
        var adjacentGroup = getAdjacentMachineGroup();
        if (adjacentGroup != null && adjacentGroup.icon() != null) {
            return adjacentGroup.icon();
        }

        AEItemKey icon = AdaptivePatternProviderBlockEntity.getResolvedProviderTerminalIcon(getProviderStack());
        return icon != null ? icon : AEItemKey.of(this.getPartItem());
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        int visibleSlots = Math.max(0, Math.min(getConfiguredPatternSlotCount(), this.getLogic().getPatternInv().size()));
        return this.getLogic().getPatternInv().getSubInventory(0, visibleSlots);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        var adjacentGroup = getAdjacentMachineGroup();
        if (adjacentGroup != null && adjacentGroup.icon() != null) {
            ItemStack adjacentIcon = adjacentGroup.icon().toStack();
            if (!adjacentIcon.isEmpty()) {
                return adjacentIcon;
            }
        }

        ItemStack icon = AdaptivePatternProviderBlockEntity.getResolvedProviderMainMenuIcon(getProviderStack());
        return icon != null && !icon.isEmpty() ? icon.copy() : new ItemStack(this.getPartItem().asItem());
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        if (this instanceof Nameable nameable && nameable.hasCustomName()) {
            return new PatternContainerGroup(this.getTerminalIcon(), nameable.getCustomName(), List.of());
        }

        var adjacentGroup = getAdjacentMachineGroup();
        if (adjacentGroup != null) {
            return adjacentGroup;
        }

        List<Component> tooltip = new ArrayList<>();
        int unlockedSlots = getConfiguredPatternSlotCount();
        int totalSlots = getCurrentProviderMaxPatternCapacity();
        if (unlockedSlots < totalSlots) {
            tooltip.add(Component.translatable(
                    "tooltip.data_energistics.adaptive_pattern_provider.terminal_hidden_slots",
                    unlockedSlots,
                    totalSlots
            ));
        }

        return new PatternContainerGroup(
                this.getTerminalIcon(),
                getProviderDisplayName(),
                List.copyOf(tooltip)
        );
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        if (inv == this.providerInventory) {
            this.getLogic().updatePatterns();
        }
        if (inv == this.upgrades) {
            this.providerInventory.setMaxStackSize(0, getProviderSlotLimit());
        }
        onAdaptiveStateChanged();
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
    }

    private int getConfiguredPatternSlotCount() {
        ItemStack providerStack = getProviderStack();
        int slotsPerProvider = AdaptivePatternProviderBlockEntity.getResolvedSlotsPerProvider(providerStack);
        if (slotsPerProvider <= 0) {
            return 0;
        }

        int providerCount = Math.min(providerStack.getCount(), getProviderSlotLimit());
        return slotsPerProvider * providerCount;
    }

    private int getCurrentProviderMaxPatternCapacity() {
        int slotsPerProvider = AdaptivePatternProviderBlockEntity.getResolvedSlotsPerProvider(getProviderStack());
        if (slotsPerProvider <= 0) {
            return 0;
        }

        return Math.min(MAX_PATTERN_SLOTS, slotsPerProvider * getProviderSlotLimit());
    }

    private int getExtraProviderSlotsFromCapacityCards() {
        if (this.upgrades == null) {
            return 0;
        }
        return Math.max(0, this.upgrades.getInstalledUpgrades(appeng.core.definitions.AEItems.CAPACITY_CARD))
                * EXTRA_PROVIDER_SLOTS_PER_CAPACITY_CARD;
    }

    private void onAdaptiveStateChanged() {
        this.saveChanges();
        markForClientUpdate();
        this.getLogic().updatePatterns();
        this.getLogic().onHostStateChanged();
        try {
            appeng.api.networking.crafting.ICraftingProvider.requestUpdate(this.getMainNode());
        } catch (Throwable ignored) {
        }
    }

    private IUpgradeInventory createUpgradeInventory() {
        Item inductionCard = AdaptivePatternProviderBlockEntity.getAppliedFluxInductionCard();
        if (inductionCard == null) {
            return UpgradeInventories.empty();
        }

        return UpgradeInventories.forMachine(
                this.getPartItem().asItem(),
                APPFLUX_UPGRADE_SLOTS,
                this::onAdaptiveStateChanged
        );
    }

    private static <E extends Enum<E>> E readEnum(CompoundTag data, String key, E fallback, Class<E> enumClass) {
        if (!data.contains(key)) {
            return fallback;
        }

        try {
            return Enum.valueOf(enumClass, data.getString(key));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    @Nullable
    private PatternContainerGroup getAdjacentMachineGroup() {
        var blockEntity = this.getBlockEntity();
        if (blockEntity == null) {
            return null;
        }

        var level = blockEntity.getLevel();
        var side = this.getSide();
        return blockEntity != null && level != null && side != null
                ? PatternContainerGroup.fromMachine(
                level,
                blockEntity.getBlockPos().relative(side),
                side.getOpposite()
        )
                : null;
    }

    private ItemStack getProviderStack() {
        return this.providerInventory != null ? this.providerInventory.getStackInSlot(0) : ItemStack.EMPTY;
    }

    private Component getResolvedProviderNameForGui() {
        ItemStack providerStack = getProviderStack();
        if (providerStack.isEmpty()) {
            return this.getPartItem().asItem().getDefaultInstance().getHoverName();
        }

        Component displayName = AdaptivePatternProviderBlockEntity.getResolvedProviderDisplayName(providerStack);
        return displayName != null ? displayName : this.getPartItem().asItem().getDefaultInstance().getHoverName();
    }

    private static final class ProviderSuffixFilter implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return AdaptivePatternProviderBlockEntity.isSupportedProviderStack(stack);
        }
    }
}
