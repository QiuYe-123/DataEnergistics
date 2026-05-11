package com.fish_dan_.data_energistics.block;

import com.fish_dan_.data_energistics.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class RedstoneCrystalBuddingBlock extends BuddingAmethystBlock {
    private static final Direction[] DIRECTIONS = Direction.values();

    public RedstoneCrystalBuddingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) != 0) {
            return;
        }

        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);
        Block block = null;

        if (canClusterGrowAtState(targetState)) {
            block = ModBlocks.SMALL_REDSTONE_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.SMALL_REDSTONE_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.MEDIUM_REDSTONE_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.MEDIUM_REDSTONE_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.LARGE_REDSTONE_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.LARGE_REDSTONE_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.REDSTONE_CRYSTAL_CLUSTER.get();
        }

        if (block == null) {
            return;
        }

        BlockState grownState = block.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
        level.setBlockAndUpdate(targetPos, grownState);
    }
}
