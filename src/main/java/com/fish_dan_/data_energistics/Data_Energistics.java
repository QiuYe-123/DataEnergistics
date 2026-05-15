package com.fish_dan_.data_energistics;

import appeng.api.AECapabilities;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.StyleManager;
import appeng.core.definitions.AEBlockEntities;
import appeng.core.definitions.AEBlockEntities;
import appeng.core.definitions.AEItems;
import appeng.init.client.InitScreens;
import appeng.items.parts.PartModelsHelper;
import com.fish_dan_.data_energistics.block.DataDistributionTowerBlock;
import com.fish_dan_.data_energistics.ae2.DataFlowBusStrategies;
import com.fish_dan_.data_energistics.ae2.ModAE2Keys;
import com.fish_dan_.data_energistics.block.AdaptivePatternProviderBlock;
import com.fish_dan_.data_energistics.blockentity.DataDistributionTowerBlockEntity;
import com.fish_dan_.data_energistics.blockentity.DataTeleportAnchorBlockEntity;
import com.fish_dan_.data_energistics.client.screen.AdaptivePatternProviderScreen;
import com.fish_dan_.data_energistics.client.screen.NativePatternEncodingTermScreen;
import com.fish_dan_.data_energistics.client.screen.PatternEncodingPreviewScreen;
import com.fish_dan_.data_energistics.client.screen.WirelessPatternEncodingTermScreen;
import com.fish_dan_.data_energistics.client.render.DataExtractorRenderer;
import com.fish_dan_.data_energistics.client.render.DispersingDataRenderer;
import com.fish_dan_.data_energistics.client.render.DataDistributionTowerRenderer;
import com.fish_dan_.data_energistics.client.render.MatterConvergingBoltRenderer;
import com.fish_dan_.data_energistics.client.screen.DataDistributionTowerScreen;
import com.fish_dan_.data_energistics.client.screen.DataExtractorScreen;
import com.fish_dan_.data_energistics.client.screen.DataMimeticFieldScreen;
import com.fish_dan_.data_energistics.client.screen.DataRipperReassemblerScreen;
import com.fish_dan_.data_energistics.client.screen.DataSolarPanelScreen;
import com.fish_dan_.data_energistics.client.screen.DataTeleportAnchorScreen;
import com.fish_dan_.data_energistics.client.ModItemColors;
import com.fish_dan_.data_energistics.client.ModKeyMappings;
import com.fish_dan_.data_energistics.item.MatterConvergingCrossbowItem;
import com.fish_dan_.data_energistics.client.screen.DataRipperScreen;
import com.fish_dan_.data_energistics.client.screen.UniversalCraftingTermScreen;
import com.fish_dan_.data_energistics.client.screen.UniversalMEStorageScreen;
import com.fish_dan_.data_energistics.client.screen.UniversalPatternAccessTermScreen;
import com.fish_dan_.data_energistics.client.screen.UniversalPatternEncodingTermScreen;
import com.fish_dan_.data_energistics.client.screen.UniversalTerminalScreenHook;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.fish_dan_.data_energistics.network.ModPayloads;
import com.fish_dan_.data_energistics.part.AdaptivePatternProviderPart;
import com.fish_dan_.data_energistics.part.AdaptivePatternProviderPart;
import com.fish_dan_.data_energistics.registry.ModBlockEntities;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.registry.ModCreativeTabs;
import com.fish_dan_.data_energistics.registry.ModEntities;
import com.fish_dan_.data_energistics.registry.ModItems;
import com.fish_dan_.data_energistics.registry.ModMenus;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import com.fish_dan_.data_energistics.registry.ModStructures;
import com.fish_dan_.data_energistics.registry.ModStorageCells;
import com.fish_dan_.data_energistics.registry.UniversalTerminalAdapters;
import com.fish_dan_.data_energistics.recipe.TimeShiftTransformLogic;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Data_Energistics.MODID)
public class Data_Energistics {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "data_energistics";
    private static final String ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP =
            "block.data_energistics.adaptive_pattern_provider";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Data_Energistics(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModStructures.register(modEventBus);
        UniversalTerminalAdapters.init();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerAe2KeyTypes);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerPayloadHandlers);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new TimeShiftTransformLogic());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, SolarPanelConfig.SPEC, "data_energistics-solar_panel.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            UniversalTerminalAdapters.discoverFromRegisteredItems();
            DataFlowBusStrategies.register();
            ((AdaptivePatternProviderBlock) ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get()).bindBlockEntity();
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_FLOW_GENERATOR_BLOCK_ENTITY.get(), ModBlocks.DATA_FLOW_GENERATOR.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_SOLAR_PANEL_BLOCK_ENTITY.get(), ModBlocks.DATA_SOLAR_PANEL.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_EXTRACTOR_BLOCK_ENTITY.get(), ModBlocks.DATA_EXTRACTOR.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(), ModBlocks.DATA_RIPPER_REASSEMBLER.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_FRAMEWORK_BLOCK_ENTITY.get(), ModBlocks.DATA_FRAMEWORK.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_DISTRIBUTION_TOWER_BLOCK_ENTITY.get(), ModBlocks.DATA_DISTRIBUTION_TOWER.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_MIMETIC_FIELD_BLOCK_ENTITY.get(), ModBlocks.DATA_MIMETIC_FIELD.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.DATA_TELEPORT_ANCHOR_BLOCK_ENTITY.get(), ModBlocks.DATA_TELEPORT_ANCHOR.get().asItem());
            AEBaseBlockEntity.registerBlockEntityItem(ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(), ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get().asItem());
            Upgrades.add(AEItems.ENERGY_CARD, ModItems.DATA_RIPPER.get(), 8, "item.data_energistics.data_ripper");
            Upgrades.add(AEItems.SPEED_CARD, ModItems.DATA_RIPPER.get(), 4, "item.data_energistics.data_ripper");
            Upgrades.add(AEItems.ENERGY_CARD, ModBlocks.DATA_EXTRACTOR.get(), 6, "block.data_energistics.data_extractor");
            Upgrades.add(AEItems.CAPACITY_CARD, ModBlocks.DATA_EXTRACTOR.get(), 6, "block.data_energistics.data_extractor");
            Upgrades.add(AEItems.SPEED_CARD, ModBlocks.DATA_EXTRACTOR.get(), 5, "block.data_energistics.data_extractor");
            Upgrades.add(AEItems.SPEED_CARD, ModBlocks.DATA_RIPPER_REASSEMBLER.get(), 4, "block.data_energistics.data_reassembler");
            Upgrades.add(AEItems.SPEED_CARD, ModBlocks.DATA_SOLAR_PANEL.get(), 3, "block.data_energistics.me_solar_panel");
            Upgrades.add(AEItems.ENERGY_CARD, ModBlocks.DATA_SOLAR_PANEL.get(), 3, "block.data_energistics.me_solar_panel");
            Upgrades.add(AEItems.CAPACITY_CARD, ModBlocks.DATA_MIMETIC_FIELD.get(), 3, "block.data_energistics.data_mimetic_field");
            Upgrades.add(AEItems.SPEED_CARD, ModBlocks.DATA_MIMETIC_FIELD.get(), 4, "block.data_energistics.data_mimetic_field");
            Upgrades.add(AEItems.CAPACITY_CARD, ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get(), 3, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            Upgrades.add(AEItems.CAPACITY_CARD, ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get(), 3, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            Upgrades.add(AEItems.SPEED_CARD, ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get(), 4, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            Upgrades.add(AEItems.SPEED_CARD, ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get(), 4, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            Upgrades.add(AEItems.ENERGY_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 2,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(AEItems.FUZZY_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 1,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(AEItems.INVERTER_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 1,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(AEItems.VOID_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 1,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(AEItems.SPEED_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 4,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(AEItems.REDSTONE_CARD, ModItems.MATTER_CONVERGING_CROSSBOW.get(), 1,
                    "item.data_energistics.matter_converging_crossbow");
            Upgrades.add(ModItems.REDSTONE_TUNING_CARD.get(), AEBlocks.PATTERN_PROVIDER.block(), 1, "block.ae2.pattern_provider");
            Upgrades.add(ModItems.REDSTONE_TUNING_CARD.get(), ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get(), 1, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            Upgrades.add(ModItems.REDSTONE_TUNING_CARD.get(), ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get(), 1,
                    ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
            registerExternalRedstoneTuningCardCompat();
            registerAppliedFluxAdaptivePatternProviderCompat();
            registerAe2CrystalScienceAdaptivePatternProviderCompat();
            appeng.api.parts.PartModels.registerModels(
                    PartModelsHelper.createModels(ModItems.DATA_RIPPER.get().getPartClass())
            );
            appeng.api.parts.PartModels.registerModels(
                    PartModelsHelper.createModels(ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get().getPartClass())
            );
            appeng.api.parts.PartModels.registerModels(
                    PartModelsHelper.createModels(ModItems.UNIVERSAL_TERMINAL.get().getPartClass())
            );
        });
    }

    private void registerAppliedFluxAdaptivePatternProviderCompat() {
        Item inductionCard = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("appflux", "induction_card"));
        if (inductionCard == null || inductionCard == Items.AIR) {
            return;
        }

        Upgrades.add(inductionCard, ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get(), 1, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
        Upgrades.add(inductionCard, ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get(), 1, ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
    }

    private void registerAe2CrystalScienceAdaptivePatternProviderCompat() {
        Item crystalGrowthCard =
                BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("ae2cs", "crystal_growth_card"));
        if (crystalGrowthCard == null || crystalGrowthCard == Items.AIR) {
            return;
        }

        Upgrades.add(crystalGrowthCard, ModBlocks.ADAPTIVE_PATTERN_PROVIDER.get(), 1,
                ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
        Upgrades.add(crystalGrowthCard, ModItems.ADAPTIVE_PATTERN_PROVIDER_PART.get(), 1,
                ADAPTIVE_PATTERN_PROVIDER_UPGRADE_TOOLTIP_GROUP);
    }

    private static void registerExternalRedstoneTuningCardCompat() {
        registerExternalRedstoneTuningTarget("advanced_ae", "small_adv_pattern_provider", "block.advanced_ae.small_adv_pattern_provider");
        registerExternalRedstoneTuningTarget("advanced_ae", "adv_pattern_provider", "block.advanced_ae.adv_pattern_provider");
        registerExternalRedstoneTuningItemTarget("advanced_ae", "small_adv_pattern_provider_part",
                "block.advanced_ae.small_adv_pattern_provider");
        registerExternalRedstoneTuningItemTarget("advanced_ae", "adv_pattern_provider_part",
                "block.advanced_ae.adv_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2lt", "overloaded_pattern_provider", "block.ae2lt.overloaded_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2cs", "simple_pattern_provider", "block.ae2cs.simple_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2cs", "resonating_pattern_provider", "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2cs", "extended_resonating_pattern_provider",
                "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2cs", "ex_resonating_pattern_provider",
                "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningTarget("ae2cs", "meteorite_pattern_provider", "block.ae2cs.meteorite_pattern_provider");
        registerExternalRedstoneTuningItemTarget("ae2cs", "simple_pattern_provider_part",
                "block.ae2cs.simple_pattern_provider");
        registerExternalRedstoneTuningItemTarget("ae2cs", "resonating_pattern_provider_part",
                "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningItemTarget("ae2cs", "extended_resonating_pattern_provider_part",
                "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningItemTarget("ae2cs", "ex_resonating_pattern_provider_part",
                "block.ae2cs.resonating_pattern_provider");
        registerExternalRedstoneTuningItemTarget("ae2cs", "meteorite_pattern_provider_part",
                "block.ae2cs.meteorite_pattern_provider");
        registerExternalRedstoneTuningTarget("appliedcreate", "andesite_pattern_provider", "block.appliedcreate.andesite_pattern_provider");
        registerExternalRedstoneTuningTarget("appliedcreate", "brass_pattern_provider", "block.appliedcreate.brass_pattern_provider");
        registerExternalRedstoneTuningItemTarget("appliedcreate", "andesite_pattern_provider_part",
                "block.appliedcreate.andesite_pattern_provider");
        registerExternalRedstoneTuningItemTarget("appliedcreate", "brass_pattern_provider_part",
                "block.appliedcreate.brass_pattern_provider");
        registerExternalRedstoneTuningTarget("extendedae", "ex_pattern_provider", "block.extendedae.ex_pattern_provider");
        registerExternalRedstoneTuningItemTarget("extendedae", "ex_pattern_provider_part",
                "block.extendedae.ex_pattern_provider");
        registerExternalRedstoneTuningTarget("megacells", "mega_pattern_provider", "block.megacells.mega_pattern_provider");
        registerExternalRedstoneTuningTarget("extendedae_plus", "mirror_pattern_provider", "block.extendedae_plus.mirror_pattern_provider");
    }

    private static void registerExternalRedstoneTuningTarget(String namespace, String path, String tooltipKey) {
        var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        if (block == null || block == Blocks.AIR) {
            return;
        }
        Upgrades.add(ModItems.REDSTONE_TUNING_CARD.get(), block, 1, tooltipKey);
    }

    private static void registerExternalRedstoneTuningItemTarget(String namespace, String path, String tooltipKey) {
        var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        if (item == null || item == Items.AIR) {
            return;
        }
        Upgrades.add(ModItems.REDSTONE_TUNING_CARD.get(), item, 1, tooltipKey);
    }

    private void registerAe2KeyTypes(final RegisterEvent event) {
        ModAE2Keys.register(event);
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_FLOW_GENERATOR_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_SOLAR_PANEL_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_EXTRACTOR_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.CRAFTING_MACHINE,
                ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.ME_STORAGE,
                ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalPatternInputStorage()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DATA_EXTRACTOR_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalInventory().toItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalInventory().toItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.DATA_RIPPER_REASSEMBLER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalFluidHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DATA_MIMETIC_FIELD_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getInternalInventory().toItemHandler()
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_MIMETIC_FIELD_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_DISTRIBUTION_TOWER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.DATA_TELEPORT_ANCHOR_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> {
                    var logic = blockEntity.getLogic();
                    return logic != null ? logic.getReturnInv() : null;
                }
        );
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalReturnItemHandler(context)
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalReturnFluidHandler(context)
        );
        event.registerBlockEntity(
                mekanism.common.capabilities.Capabilities.CHEMICAL.block(),
                ModBlockEntities.ADAPTIVE_PATTERN_PROVIDER_BLOCK_ENTITY.get(),
                (blockEntity, context) -> blockEntity.getExternalReturnChemicalHandler(context)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AEBlockEntities.CABLE_BUS.get(),
                (CableBusBlockEntity blockEntity, Direction context) -> {
                    if (context == null) {
                        return null;
                    }

                    var part = blockEntity.getPart(context);
                    if (part instanceof AdaptivePatternProviderPart adaptivePart) {
                        return adaptivePart.getExternalReturnItemHandler();
                    }

                    return null;
                }
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                AEBlockEntities.CABLE_BUS.get(),
                (CableBusBlockEntity blockEntity, Direction context) -> {
                    if (context == null) {
                        return null;
                    }

                    var part = blockEntity.getPart(context);
                    if (part instanceof AdaptivePatternProviderPart adaptivePart) {
                        return adaptivePart.getExternalReturnFluidHandler();
                    }

                    return null;
                }
        );
        event.registerBlockEntity(
                mekanism.common.capabilities.Capabilities.CHEMICAL.block(),
                AEBlockEntities.CABLE_BUS.get(),
                (CableBusBlockEntity blockEntity, Direction context) -> {
                    if (context == null) {
                        return null;
                    }

                    var part = blockEntity.getPart(context);
                    if (part instanceof AdaptivePatternProviderPart adaptivePart) {
                        return adaptivePart.getExternalReturnChemicalHandler();
                    }

                    return null;
                }
        );
        event.registerBlockEntity(
                AECapabilities.CRANKABLE,
                AEBlockEntities.CONTROLLER.get(),
                (ControllerBlockEntity blockEntity, Direction context) ->
                        context != null ? ((AENetworkedPoweredBlockEntity) blockEntity).new Crankable() : null
        );
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, blockEntity, context) -> {
                    if (!(state.getBlock() instanceof DataDistributionTowerBlock)) {
                        return null;
                    }

                    BlockPos basePos = DataDistributionTowerBlock.getBasePos(pos, state);
                    BlockState baseState = level.getBlockState(basePos);
                    if (!(baseState.getBlock() instanceof DataDistributionTowerBlock)
                            || !(level.getBlockEntity(basePos) instanceof DataDistributionTowerBlockEntity tower)) {
                        return null;
                    }

                    return tower.getEnergyStorageForQuery(pos, context);
                },
                ModBlocks.DATA_DISTRIBUTION_TOWER.get()
        );

    }

    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        ModPayloads.register(event);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @SuppressWarnings("removal")
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            ModItemColors.register(event);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ModAE2Keys.registerClient();
                ModStorageCells.registerClientModels();
                registerMatterConvergingCrossbowProperties();
                NeoForge.EVENT_BUS.addListener(ClientModEvents::onScreenOpening);
                NeoForge.EVENT_BUS.addListener(ClientModEvents::onScreenInitPost);
                NeoForge.EVENT_BUS.addListener(ClientModEvents::onScreenRenderPost);
            });
        }

        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(ModKeyMappings.OPEN_PATTERN_PROVIDER);
            event.register(ModKeyMappings.RENAME_PATTERN_PROVIDER);
        }

        @SubscribeEvent
        public static void onRegisterScreens(RegisterMenuScreensEvent event) {
            InitScreens.register(event, ModMenus.DATA_RIPPER.get(), DataRipperScreen::new, "/screens/data_ripper.json");
            InitScreens.register(event, ModMenus.DATA_DISTRIBUTION_TOWER.get(), DataDistributionTowerScreen::new, "/screens/data_distribution_tower.json");
            InitScreens.register(event, ModMenus.DATA_EXTRACTOR.get(), DataExtractorScreen::new, "/screens/data_extractor.json");
            InitScreens.register(event, ModMenus.DATA_RIPPER_REASSEMBLER.get(), DataRipperReassemblerScreen::new, "/screens/data_reassembler.json");
            InitScreens.register(event, ModMenus.DATA_MIMETIC_FIELD.get(), DataMimeticFieldScreen::new, "/screens/data_mimetic_field.json");
            InitScreens.register(event, ModMenus.DATA_SOLAR_PANEL.get(), DataSolarPanelScreen::new, "/screens/me_solar_panel.json");
            InitScreens.register(event, ModMenus.DATA_TELEPORT_ANCHOR.get(), DataTeleportAnchorScreen::new, "/screens/data_teleport_anchor.json");
            InitScreens.register(event, ModMenus.ADAPTIVE_PATTERN_PROVIDER.get(), AdaptivePatternProviderScreen::new, "/screens/adaptive_pattern_provider.json");
            InitScreens.register(event, ModMenus.UNIVERSAL_ME_STORAGE.get(), UniversalMEStorageScreen::new, "/screens/universal_me_storage_terminal.json");
            InitScreens.register(event, ModMenus.UNIVERSAL_CRAFTING_TERM.get(), UniversalCraftingTermScreen::new, "/screens/universal_crafting_terminal.json");
            InitScreens.register(event, ModMenus.UNIVERSAL_PATTERN_ENCODING_TERM.get(), UniversalPatternEncodingTermScreen::new, "/screens/universal_pattern_encoding_terminal.json");
            InitScreens.register(event, ModMenus.UNIVERSAL_PATTERN_ACCESS_TERM.get(), UniversalPatternAccessTermScreen::new, "/screens/universal_pattern_access_terminal.json");
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.DATA_EXTRACTOR_BLOCK_ENTITY.get(), DataExtractorRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.DATA_DISTRIBUTION_TOWER_BLOCK_ENTITY.get(), DataDistributionTowerRenderer::new);
            event.registerEntityRenderer(ModEntities.DISPERSING_DATA.get(), DispersingDataRenderer::new);
            event.registerEntityRenderer(ModEntities.MATTER_CONVERGING_BOLT.get(), MatterConvergingBoltRenderer::new);
        }

        private static void registerMatterConvergingCrossbowProperties() {
            var item = ModItems.MATTER_CONVERGING_CROSSBOW.get();
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pull"),
                    (stack, level, entity, seed) -> {
                        if (entity == null) {
                            return 0.0F;
                        }
                        if (net.minecraft.world.item.CrossbowItem.isCharged(stack)) {
                            return 0.0F;
                        }
                        return entity.getUseItem() != stack
                                ? 0.0F
                                : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())
                                        / (float) MatterConvergingCrossbowItem.getChargeDuration(stack, entity);
                    });
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("pulling"),
                    (stack, level, entity, seed) -> entity != null
                            && entity.isUsingItem()
                            && entity.getUseItem() == stack
                            && !net.minecraft.world.item.CrossbowItem.isCharged(stack)
                                    ? 1.0F
                                    : 0.0F);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("charged"),
                    (stack, level, entity, seed) -> net.minecraft.world.item.CrossbowItem.isCharged(stack) ? 1.0F : 0.0F);
            ItemProperties.register(item, ResourceLocation.withDefaultNamespace("firework"),
                    (stack, level, entity, seed) -> {
                        var charged = stack.get(net.minecraft.core.component.DataComponents.CHARGED_PROJECTILES);
                        return charged != null && charged.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
                    });
        }

        public static void onScreenInitPost(ScreenEvent.Init.Post event) {
            maybeReplaceNativePatternEncodingScreen(event.getScreen(), true);
            maybeReplaceWirelessPatternEncodingScreen(event.getScreen(), true);
            UniversalTerminalScreenHook.onScreenInitPost(event);
        }

        public static void onScreenOpening(ScreenEvent.Opening event) {
            Screen replacement = maybeReplaceNativePatternEncodingScreen(event.getCurrentScreen(), false);
            if (replacement == null) {
                replacement = maybeReplaceWirelessPatternEncodingScreen(event.getCurrentScreen(), false);
            }
            if (replacement != null) {
                event.setNewScreen(replacement);
            }
        }

        public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
            UniversalTerminalScreenHook.onScreenRenderPost(event);
        }

        private static Screen maybeReplaceNativePatternEncodingScreen(Screen currentScreen, boolean applyImmediately) {
            if (!(currentScreen instanceof PatternEncodingTermScreen<?> screen)) {
                return null;
            }

            if (currentScreen instanceof PatternEncodingPreviewScreen<?>) {
                return null;
            }

            if (!(screen.getMenu() instanceof PatternEncodingPreviewMenu)) {
                return null;
            }

            if (currentScreen.getClass() != PatternEncodingTermScreen.class) {
                return null;
            }

            Screen replacement = new NativePatternEncodingTermScreen(
                    screen.getMenu(),
                    screen.getMenu().getPlayerInventory(),
                    screen.getTitle(),
                    StyleManager.loadStyleDoc("/screens/terminals/pattern_encoding_terminal.json"));

            if (applyImmediately) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen == currentScreen) {
                    minecraft.setScreen(replacement);
                }
            }

            return replacement;
        }

        private static Screen maybeReplaceWirelessPatternEncodingScreen(Screen currentScreen, boolean applyImmediately) {
            if (currentScreen instanceof WirelessPatternEncodingTermScreen) {
                return null;
            }
            if (!(currentScreen instanceof de.mari_023.ae2wtlib.wet.WETScreen screen)) {
                return null;
            }
            if (!(screen.getMenu() instanceof PatternEncodingPreviewMenu)) {
                return null;
            }
            if (!(screen.getMenu() instanceof de.mari_023.ae2wtlib.wet.WETMenu wetMenu)) {
                return null;
            }

            Screen replacement = new WirelessPatternEncodingTermScreen(
                    wetMenu,
                    screen.getMenu().getPlayerInventory(),
                    screen.getTitle(),
                    StyleManager.loadStyleDoc("/screens/wtlib/wireless_pattern_encoding_terminal.json"));
            if (applyImmediately) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen == currentScreen) {
                    minecraft.setScreen(replacement);
                }
            }
            return replacement;
        }
    }
}
