package com.fish_dan_.data_energistics.client.render;

import com.fish_dan_.data_energistics.entity.ThrownLightSaberEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ThrownLightSaberRenderer extends ThrownItemRenderer<ThrownLightSaberEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownLightSaberRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, true);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownLightSaberEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) - 90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        if (entity.isEmbedded()) {
            poseStack.translate(0.0D, 0.0D, 0.0D);
        }
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
    }
}
