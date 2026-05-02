package com.fish_dan_.data_energistics.ae2;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import com.fish_dan_.data_energistics.blockentity.AdaptivePatternProviderBlockEntity;
import com.moakiee.ae2lt.blockentity.OverloadedPatternProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public interface AdaptivePatternProviderHost extends PatternProviderLogicHost, IUpgradeableObject {
    AppEngInternalInventory getProviderInventory();

    int getProviderSlotLimit();

    ItemStack extractProviderOverflow();

    int getPatternSlotCountForMenu();

    Component getProviderDisplayName();

    Component getGuiDisplayName();

    boolean isMeteoriteProviderSelected();

    boolean isAdvancedAeProviderSelected();

    boolean isAe2LightningTechOverloadedProviderSelected();

    boolean supportsFilteredImportToggle();

    AdaptivePatternProviderBlockEntity.Ae2LtProviderMode getAe2LtProviderMode();

    void cycleAe2LtProviderMode();

    boolean isAe2LtWirelessMode();

    AdaptivePatternProviderBlockEntity.Ae2LtReturnMode getAe2LtReturnMode();

    void cycleAe2LtReturnMode();

    AdaptivePatternProviderBlockEntity.Ae2LtWirelessDispatchMode getAe2LtWirelessDispatchMode();

    void cycleAe2LtWirelessDispatchMode();

    AdaptivePatternProviderBlockEntity.Ae2LtWirelessSpeedMode getAe2LtWirelessSpeedMode();

    void cycleAe2LtWirelessSpeedMode();

    boolean isAdvancedAeFilteredImportEnabled();

    void setAdvancedAeFilteredImportEnabled(boolean enabled);

    void addOrUpdateConnection(ResourceKey<Level> dimension, BlockPos pos, Direction boundFace);

    boolean removeConnection(ResourceKey<Level> dimension, BlockPos pos);

    List<OverloadedPatternProviderBlockEntity.WirelessConnection> getConnections();

    void markForClientUpdate();
}
