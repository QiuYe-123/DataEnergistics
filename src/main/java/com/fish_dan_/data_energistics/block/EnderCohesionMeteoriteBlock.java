package com.fish_dan_.data_energistics.block;

import appeng.core.definitions.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class EnderCohesionMeteoriteBlock extends Block {
    private final float enderDustChance;
    private final float skyDustChance;
    private final float teleportChance;

    public EnderCohesionMeteoriteBlock(Properties properties, float enderDustChance, float skyDustChance, float teleportChance) {
        super(properties);
        this.enderDustChance = enderDustChance;
        this.skyDustChance = skyDustChance;
        this.teleportChance = teleportChance;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (!(level instanceof ServerLevel serverLevel) || !tool.isCorrectToolForDrops(state)) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        if (random.nextFloat() < this.enderDustChance) {
            popResource(serverLevel, pos, new ItemStack(AEItems.ENDER_DUST.asItem()));
        }
        if (random.nextFloat() < this.skyDustChance) {
            popResource(serverLevel, pos, new ItemStack(AEItems.SKY_DUST.asItem()));
        }
        if (this.teleportChance > 0.0F && random.nextFloat() < this.teleportChance && player instanceof ServerPlayer serverPlayer) {
            teleportRandomly(serverLevel, serverPlayer, random);
        }
    }

    private static void teleportRandomly(ServerLevel level, ServerPlayer player, RandomSource random) {
        BlockPos origin = player.blockPosition();
        for (int i = 0; i < 16; i++) {
            int x = origin.getX() + random.nextIntBetweenInclusive(-32, 32);
            int z = origin.getZ() + random.nextIntBetweenInclusive(-32, 32);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, origin.getY(), z));
            BlockPos target = surface.above();
            if (target.getY() <= level.getMinBuildHeight() || target.getY() >= level.getMaxBuildHeight() - 2) {
                continue;
            }
            if (!level.getBlockState(target).isAir() || !level.getBlockState(target.above()).isAir()) {
                continue;
            }

            player.teleportTo(level, target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, java.util.Set.of(), player.getYRot(), player.getXRot());
            player.fallDistance = 0.0F;
            return;
        }
    }
}
