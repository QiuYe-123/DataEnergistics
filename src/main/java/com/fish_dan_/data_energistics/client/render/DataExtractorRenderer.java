package com.fish_dan_.data_energistics.client.render;

import com.fish_dan_.data_energistics.block.DataExtractorBlock;
import com.fish_dan_.data_energistics.blockentity.DataExtractorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DataExtractorRenderer implements BlockEntityRenderer<DataExtractorBlockEntity> {
    private static final ResourceLocation INSERTED_DISK_MODEL =
            ResourceLocation.fromNamespaceAndPath("data_energistics", "block/drive/cells/data_carrier");
    private static final ModelResourceLocation INSERTED_DISK_MODEL_LOCATION =
            ModelResourceLocation.standalone(INSERTED_DISK_MODEL);
    private static final float DISK_CENTER_X = 0.5f;
    private static final float DISK_CENTER_Y = 0.90f - (1.0f / 16.0f);
    private static final float DISK_FORWARD_OFFSET = 0.20f + (1.0f / 16.0f);
    private static final float DISK_LEFT_OFFSET = 8.0f / 16.0f;
    private static final float DISK_SCALE = 1.0f / 16.0f;

    public DataExtractorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DataExtractorBlockEntity blockEntity) {
        return blockEntity.isRangeDisplayEnabled();
    }

    @Override
    public void render(@NotNull DataExtractorBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderInsertedCarrierDisk(blockEntity, poseStack, buffer, packedLight, packedOverlay);

        if (blockEntity.isRangeDisplayEnabled()) {
            AABB aabb = blockEntity.getCoverageAabb().move(
                    -blockEntity.getBlockPos().getX(),
                    -blockEntity.getBlockPos().getY(),
                    -blockEntity.getBlockPos().getZ()
            );

            var consumer = buffer.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(poseStack, consumer, aabb, 1.0f, 0.35f, 0.2f, 0.9f);
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull DataExtractorBlockEntity blockEntity) {
        if (!blockEntity.isRangeDisplayEnabled()) {
            return new AABB(blockEntity.getBlockPos()).inflate(0.25d, 0.5d, 0.25d);
        }
        return blockEntity.getCoverageAabb();
    }

    private void renderInsertedCarrierDisk(DataExtractorBlockEntity blockEntity, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack carrier = blockEntity.getStorageInventory().getStackInSlot(0);
        if (carrier.isEmpty()) {
            return;
        }

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(INSERTED_DISK_MODEL_LOCATION);
        Direction facing = blockEntity.getBlockState().hasProperty(DataExtractorBlock.FACING)
                ? blockEntity.getBlockState().getValue(DataExtractorBlock.FACING)
                : Direction.NORTH;

        poseStack.pushPose();
        Direction left = facing.getCounterClockWise();
        poseStack.translate(
                DISK_CENTER_X + facing.getStepX() * DISK_FORWARD_OFFSET + left.getStepX() * DISK_LEFT_OFFSET,
                DISK_CENTER_Y,
                DISK_CENTER_X + facing.getStepZ() * DISK_FORWARD_OFFSET + left.getStepZ() * DISK_LEFT_OFFSET
        );
        poseStack.mulPose(Axis.YP.rotationDegrees(getDiskRotationDegrees(facing)));
        poseStack.scale(DISK_SCALE, DISK_SCALE, DISK_SCALE);

        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());
        RandomSource random = RandomSource.create(42L);
        renderModelQuads(model.getQuads(null, null, random), poseStack, consumer, packedLight, packedOverlay);
        for (Direction side : Direction.values()) {
            random.setSeed(42L);
            renderModelQuads(model.getQuads(null, side, random), poseStack, consumer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void renderModelQuads(List<BakedQuad> quads, PoseStack poseStack, VertexConsumer consumer,
            int packedLight, int packedOverlay) {
        for (BakedQuad quad : quads) {
            consumer.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
        }
    }

    private float getDiskRotationDegrees(Direction facing) {
        return switch (facing) {
            case NORTH -> 180.0f;
            case SOUTH -> 0.0f;
            case WEST -> 90.0f;
            case EAST -> -90.0f;
            default -> 0.0f;
        };
    }
}
