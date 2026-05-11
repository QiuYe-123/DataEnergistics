package com.fish_dan_.data_energistics.client.render;

import com.fish_dan_.data_energistics.entity.DispersingDataEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DispersingDataRenderer extends EntityRenderer<DispersingDataEntity> {
    private static final ResourceLocation ORB_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(ORB_TEXTURE);
    private static final int RED = 0x87;
    private static final int GREEN = 0x5F;
    private static final int BLUE = 0xFF;

    public DispersingDataRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.12F;
        this.shadowStrength = 0.4F;
    }

    @Override
    protected int getBlockLightLevel(DispersingDataEntity entity, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(entity, pos) + 7, 0, 15);
    }

    @Override
    public void render(DispersingDataEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float bob = Mth.sin((entity.tickCount + partialTick) * 0.15F) * 0.1F;
        poseStack.translate(0.0F, 0.15F + bob, 0.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.35F, 0.35F, 0.35F);

        VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose pose = poseStack.last();
        vertex(vertexConsumer, pose, -0.5F, -0.25F, 0.0F, 0.25F, packedLight);
        vertex(vertexConsumer, pose, 0.5F, -0.25F, 0.25F, 0.25F, packedLight);
        vertex(vertexConsumer, pose, 0.5F, 0.75F, 0.25F, 0.0F, packedLight);
        vertex(vertexConsumer, pose, -0.5F, 0.75F, 0.0F, 0.0F, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v,
            int packedLight) {
        consumer.addVertex(pose, x, y, 0.0F)
                .setColor(RED, GREEN, BLUE, 200)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(DispersingDataEntity entity) {
        return ORB_TEXTURE;
    }
}
