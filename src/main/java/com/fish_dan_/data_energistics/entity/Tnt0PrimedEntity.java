package com.fish_dan_.data_energistics.entity;

import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class Tnt0PrimedEntity extends PrimedTnt {
    private static final String TAG_ORIGIN = "Origin";
    private BlockPos origin = BlockPos.ZERO;

    public Tnt0PrimedEntity(EntityType<? extends Tnt0PrimedEntity> entityType, Level level) {
        super(entityType, level);
    }

    public Tnt0PrimedEntity(Level level, BlockPos origin, @Nullable LivingEntity owner) {
        super(ModEntities.TNT_0_PRIMED.get(), level);
        this.origin = origin.immutable();
        this.setPos(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D);
        double angle = level.random.nextDouble() * (Math.PI * 2.0D);
        this.setDeltaMovement(-Math.sin(angle) * 0.02D, 0.2D, -Math.cos(angle) * 0.02D);
        this.setFuse(80);
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.setBlockState(ModBlocks.TNT_0.get().defaultBlockState());
    }

    @Override
    protected void explode() {
        if (this.level().isClientSide) {
            return;
        }

        Level level = this.level();
        BlockPos center = this.origin;
        int minY = Math.max(level.getMinBuildHeight(), center.getY());
        int maxY = Math.min(level.getMaxBuildHeight() - 1, center.getY() + 24);
        int grassY = center.getY() - 1;
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;

        for (int chunkX = centerChunkX - 1; chunkX <= centerChunkX + 1; chunkX++) {
            for (int chunkZ = centerChunkZ - 1; chunkZ <= centerChunkZ + 1; chunkZ++) {
                BlockPos chunkOrigin = new BlockPos(chunkX << 4, center.getY(), chunkZ << 4);
                if (!level.hasChunkAt(chunkOrigin)) {
                    continue;
                }

                int startX = chunkX << 4;
                int startZ = chunkZ << 4;
                for (int x = startX; x < startX + 16; x++) {
                    for (int z = startZ; z < startZ + 16; z++) {
                        for (int y = minY; y <= maxY; y++) {
                            BlockPos targetPos = new BlockPos(x, y, z);
                            if (!level.getBlockState(targetPos).isAir()) {
                                level.removeBlock(targetPos, false);
                            }
                        }

                        if (grassY >= level.getMinBuildHeight() && grassY < level.getMaxBuildHeight()) {
                            level.setBlock(new BlockPos(x, grassY, z), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong(TAG_ORIGIN, this.origin.asLong());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_ORIGIN)) {
            this.origin = BlockPos.of(tag.getLong(TAG_ORIGIN));
        }
    }
}
