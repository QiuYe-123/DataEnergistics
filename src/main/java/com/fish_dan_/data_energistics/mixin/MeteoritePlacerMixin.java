package com.fish_dan_.data_energistics.mixin;

import appeng.worldgen.meteorite.MeteoritePlacer;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(MeteoritePlacer.class)
public abstract class MeteoritePlacerMixin {
    @Shadow
    @Final
    private LevelAccessor level;

    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int y;

    @Shadow
    @Final
    private int z;

    @Shadow
    @Final
    private BoundingBox boundingBox;

    @Inject(method = "placeMeteoriteSkyStone", at = @At("TAIL"))
    private void dataEnergistics$injectRedstoneCrystalMotherRock(CallbackInfo ci) {
        if (this.random.nextFloat() > 0.45F) {
            return;
        }

        List<BlockPos> corePositions = this.dataEnergistics$corePositions();
        if (corePositions.isEmpty()) {
            return;
        }

        Collections.shuffle(corePositions, new java.util.Random(this.random.nextLong()));

        int motherRockCount = Math.min(corePositions.size() - 1, 1 + this.random.nextInt(2));
        if (motherRockCount <= 0) {
            motherRockCount = 1;
        }

        for (int i = 0; i < motherRockCount; i++) {
            this.level.setBlock(corePositions.get(i), ModBlocks.BUDDING_REDSTONE_CRYSTAL.get().defaultBlockState(), 3);
        }

        BlockPos crystalBlockPos = corePositions.get(motherRockCount);
        this.level.setBlock(crystalBlockPos, ModBlocks.REDSTONE_CRYSTAL_BLOCK.get().defaultBlockState(), 3);

        BlockPos clusterPos = crystalBlockPos.above();
        if (this.boundingBox.isInside(clusterPos)) {
            this.level.setBlock(clusterPos, this.dataEnergistics$clusterState(), 3);
        }
    }

    @Unique
    private List<BlockPos> dataEnergistics$corePositions() {
        int[][] offsets = {
                {-1, -1},
                {-1, 0},
                {-1, 1},
                {0, -1},
                {0, 1},
                {1, -1},
                {1, 0},
                {1, 1}
        };
        List<BlockPos> positions = new ArrayList<>(offsets.length);
        for (int[] offset : offsets) {
            BlockPos pos = new BlockPos(this.x + offset[0], this.y - 1, this.z + offset[1]);
            if (this.boundingBox.isInside(pos)) {
                positions.add(pos);
            }
        }
        return positions;
    }

    @Unique
    private BlockState dataEnergistics$clusterState() {
        return ModBlocks.REDSTONE_CRYSTAL_CLUSTER.get().defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, Direction.UP)
                .setValue(AmethystClusterBlock.WATERLOGGED, this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).getType() == Fluids.WATER);
    }
}
