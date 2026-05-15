package com.fish_dan_.data_energistics.registry;

import com.fish_dan_.data_energistics.block.AdaptivePatternProviderBlock;
import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.block.DataFlowGeneratorBlock;
import com.fish_dan_.data_energistics.block.DataSolarPanelBlock;
import com.fish_dan_.data_energistics.block.DataDistributionTowerBlock;
import com.fish_dan_.data_energistics.block.DataExtractorBlock;
import com.fish_dan_.data_energistics.block.DataFrameworkBlock;
import com.fish_dan_.data_energistics.block.DataMimeticFieldBlock;
import com.fish_dan_.data_energistics.block.EnderCohesionMeteoriteBlock;
import com.fish_dan_.data_energistics.block.DataRipperReassemblerBlock;
import com.fish_dan_.data_energistics.block.DataTeleportAnchorBlock;
import com.fish_dan_.data_energistics.block.RedstoneCrystalBuddingBlock;
import com.fish_dan_.data_energistics.block.ResidualDataOreBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Data_Energistics.MODID);

    public static final DeferredBlock<Block> DATA_FLOW_GENERATOR = BLOCKS.registerBlock(
            "data_flow_generator",
            DataFlowGeneratorBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> DATA_SOLAR_PANEL = BLOCKS.registerBlock(
            "me_solar_panel",
            DataSolarPanelBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK)
                    .noOcclusion());

    public static final DeferredBlock<Block> DATA_EXTRACTOR = BLOCKS.registerBlock(
            "data_extractor",
            DataExtractorBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> DATA_RIPPER_REASSEMBLER = BLOCKS.registerBlock(
            "data_reassembler",
            DataRipperReassemblerBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> DATA_FRAMEWORK = BLOCKS.registerBlock(
            "data_framework",
            DataFrameworkBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.QUARTZ_BLOCK));

    public static final DeferredBlock<Block> DATA_DISTRIBUTION_TOWER = BLOCKS.registerBlock(
            "data_distribution_tower",
            DataDistributionTowerBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(DataDistributionTowerBlock.PART) == 2 ? 15 : 0));

    public static final DeferredBlock<Block> DATA_MIMETIC_FIELD = BLOCKS.registerBlock(
            "data_mimetic_field",
            DataMimeticFieldBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> DATA_TELEPORT_ANCHOR = BLOCKS.registerBlock(
            "data_teleport_anchor",
            DataTeleportAnchorBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> ADAPTIVE_PATTERN_PROVIDER = BLOCKS.registerBlock(
            "adaptive_pattern_provider",
            properties -> new AdaptivePatternProviderBlock(properties),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<Block> RESIDUAL_DATA_ORE = BLOCKS.registerBlock(
            "residual_data_ore",
            ResidualDataOreBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.ANCIENT_DEBRIS)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> ENDER_COHESION_METEORITE_0 = BLOCKS.registerBlock(
            "data_cohesion_meteorite_0",
            properties -> new EnderCohesionMeteoriteBlock(properties, 0.05F, 0.00F, 0.00F),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.ANCIENT_DEBRIS)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> ENDER_COHESION_METEORITE_1 = BLOCKS.registerBlock(
            "data_cohesion_meteorite_1",
            properties -> new EnderCohesionMeteoriteBlock(properties, 0.10F, 0.00F, 0.00F),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.ANCIENT_DEBRIS)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> ENDER_COHESION_METEORITE_2 = BLOCKS.registerBlock(
            "data_cohesion_meteorite_2",
            properties -> new EnderCohesionMeteoriteBlock(properties, 0.15F, 0.00F, 0.15F),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.ANCIENT_DEBRIS)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> REDSTONE_CRYSTAL_BLOCK = BLOCKS.registerBlock(
            "redstone_crystal_block",
            Block::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.REDSTONE_BLOCK)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> BUDDING_REDSTONE_CRYSTAL = BLOCKS.registerBlock(
            "budding_redstone_crystal",
            RedstoneCrystalBuddingBlock::new,
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.BUDDING_AMETHYST)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> SMALL_REDSTONE_CRYSTAL_BUD = BLOCKS.registerBlock(
            "small_redstone_crystal_bud",
            properties -> new AmethystClusterBlock(3.0F, 4.0F, properties),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.SMALL_AMETHYST_BUD)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> MEDIUM_REDSTONE_CRYSTAL_BUD = BLOCKS.registerBlock(
            "medium_redstone_crystal_bud",
            properties -> new AmethystClusterBlock(4.0F, 3.0F, properties),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.MEDIUM_AMETHYST_BUD)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> LARGE_REDSTONE_CRYSTAL_BUD = BLOCKS.registerBlock(
            "large_redstone_crystal_bud",
            properties -> new AmethystClusterBlock(5.0F, 3.0F, properties),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.LARGE_AMETHYST_BUD)
                    .requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> REDSTONE_CRYSTAL_CLUSTER = BLOCKS.registerBlock(
            "redstone_crystal_cluster",
            properties -> new AmethystClusterBlock(7.0F, 3.0F, properties),
            BlockBehaviour.Properties.ofLegacyCopy(Blocks.AMETHYST_CLUSTER)
                    .requiresCorrectToolForDrops());

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
