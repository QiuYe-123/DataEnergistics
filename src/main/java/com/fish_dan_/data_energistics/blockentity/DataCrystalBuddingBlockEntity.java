package com.fish_dan_.data_energistics.blockentity;

import appeng.blockentity.AEBaseBlockEntity;
import com.fish_dan_.data_energistics.registry.ModBlockEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DataCrystalBuddingBlockEntity extends AEBaseBlockEntity {
    private static final String GROWTH_COUNT_TAG = "GrowthCount";
    private int growthCount;

    public DataCrystalBuddingBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.DATA_CRYSTAL_BUDDING_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public int getGrowthCount() {
        return this.growthCount;
    }

    public void setGrowthCount(int growthCount) {
        this.growthCount = growthCount;
        this.setChanged();
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.growthCount = data.getInt(GROWTH_COUNT_TAG);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        data.putInt(GROWTH_COUNT_TAG, this.growthCount);
    }
}
