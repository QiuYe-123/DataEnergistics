package com.fish_dan_.data_energistics.block;

import com.fish_dan_.data_energistics.blockentity.DataCrystalBuddingBlockEntity;
import com.fish_dan_.data_energistics.registry.ModBlockEntities;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class DataCrystalBuddingBlock extends BuddingAmethystBlock implements EntityBlock {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int DOWNGRADE_THRESHOLD = 16;
    private static final float DOWNGRADE_CHANCE = 0.2F;

    public DataCrystalBuddingBlock(BlockBehaviour.Properties properties) {
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
            block = ModBlocks.SMALL_DATA_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.SMALL_DATA_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.MEDIUM_DATA_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.MEDIUM_DATA_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.LARGE_DATA_CRYSTAL_BUD.get();
        } else if (targetState.is(ModBlocks.LARGE_DATA_CRYSTAL_BUD.get())
                && targetState.getValue(AmethystClusterBlock.FACING) == direction) {
            block = ModBlocks.DATA_CRYSTAL_CLUSTER.get();
        }

        if (block == null) {
            return;
        }

        BlockState grownState = block.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
        level.setBlockAndUpdate(targetPos, grownState);
        this.onGrowthSucceeded(level, pos, state, random);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DataCrystalBuddingBlockEntity(blockPos, blockState);
    }

    private void onGrowthSucceeded(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        int tier = this.getMotherrockTier(state);
        if (tier < 1 || tier > 3) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DataCrystalBuddingBlockEntity buddingBlockEntity)) {
            return;
        }

        int growthCount = buddingBlockEntity.getGrowthCount() + 1;
        if (growthCount < DOWNGRADE_THRESHOLD) {
            buddingBlockEntity.setGrowthCount(growthCount);
            return;
        }

        buddingBlockEntity.setGrowthCount(0);
        if (random.nextFloat() < DOWNGRADE_CHANCE) {
            level.setBlockAndUpdate(pos, this.getDowngradedState(tier));
        }
    }

    private int getMotherrockTier(BlockState state) {
        if (state.is(ModBlocks.BUDDING_DATA_CRYSTAL_1.get())) {
            return 1;
        }
        if (state.is(ModBlocks.BUDDING_DATA_CRYSTAL_2.get())) {
            return 2;
        }
        if (state.is(ModBlocks.BUDDING_DATA_CRYSTAL_3.get())) {
            return 3;
        }
        if (state.is(ModBlocks.BUDDING_DATA_CRYSTAL_4.get())) {
            return 4;
        }
        return -1;
    }

    private BlockState getDowngradedState(int tier) {
        return switch (tier) {
            case 1 -> ModBlocks.BUDDING_DATA_CRYSTAL_0.get().defaultBlockState();
            case 2 -> ModBlocks.BUDDING_DATA_CRYSTAL_1.get().defaultBlockState();
            case 3 -> ModBlocks.BUDDING_DATA_CRYSTAL_2.get().defaultBlockState();
            default -> ModBlocks.BUDDING_DATA_CRYSTAL_4.get().defaultBlockState();
        };
    }
}
