package com.fish_dan_.data_energistics.integration.jade;

import com.fish_dan_.data_energistics.block.DataDistributionTowerBlock;
import com.fish_dan_.data_energistics.block.DataExtractorBlock;
import com.fish_dan_.data_energistics.block.DataMimeticFieldBlock;
import com.fish_dan_.data_energistics.block.DataSolarPanelBlock;
import com.fish_dan_.data_energistics.blockentity.DataDistributionTowerBlockEntity;
import com.fish_dan_.data_energistics.blockentity.DataExtractorBlockEntity;
import com.fish_dan_.data_energistics.blockentity.DataMimeticFieldBlockEntity;
import com.fish_dan_.data_energistics.blockentity.DataSolarPanelBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DataEnergisticsJadePlugin implements IWailaPlugin {
    private static final DataDistributionTowerJadeProvider TOWER_PROVIDER = new DataDistributionTowerJadeProvider();
    private static final DataExtractorJadeProvider EXTRACTOR_PROVIDER = new DataExtractorJadeProvider();
    private static final DataMimeticFieldJadeProvider MIMETIC_FIELD_PROVIDER = new DataMimeticFieldJadeProvider();
    private static final DataSolarPanelJadeProvider SOLAR_PANEL_PROVIDER = new DataSolarPanelJadeProvider();

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(TOWER_PROVIDER, DataDistributionTowerBlockEntity.class);
        registration.registerBlockDataProvider(EXTRACTOR_PROVIDER, DataExtractorBlockEntity.class);
        registration.registerBlockDataProvider(MIMETIC_FIELD_PROVIDER, DataMimeticFieldBlockEntity.class);
        registration.registerBlockDataProvider(SOLAR_PANEL_PROVIDER, DataSolarPanelBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(TOWER_PROVIDER, DataDistributionTowerBlock.class);
        registration.registerBlockComponent(EXTRACTOR_PROVIDER, DataExtractorBlock.class);
        registration.registerBlockComponent(MIMETIC_FIELD_PROVIDER, DataMimeticFieldBlock.class);
        registration.registerBlockComponent(SOLAR_PANEL_PROVIDER, DataSolarPanelBlock.class);
    }
}
