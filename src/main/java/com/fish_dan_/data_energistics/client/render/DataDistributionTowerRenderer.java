package com.fish_dan_.data_energistics.client.render;

import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.blockentity.DataDistributionTowerBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DataDistributionTowerRenderer implements BlockEntityRenderer<DataDistributionTowerBlockEntity> {
    private static final float CORE_BASE_Y = 2.8125f;
    private static final float CORE_FILL_SIZE = 0.375f;
    private static final float CORE_SIZE = 0.379f;
    private static final int ONLINE_FILL_RED = 196;
    private static final int ONLINE_FILL_GREEN = 255;
    private static final int ONLINE_FILL_BLUE = 255;
    private static final int OFFLINE_FILL_RED = 72;
    private static final int OFFLINE_FILL_GREEN = 96;
    private static final int OFFLINE_FILL_BLUE = 104;
    private static final int ONLINE_TEXTURE_TINT = 255;
    private static final int OFFLINE_TEXTURE_RED = 132;
    private static final int OFFLINE_TEXTURE_GREEN = 148;
    private static final int OFFLINE_TEXTURE_BLUE = 156;
    private static final double RENDER_BOX_HEIGHT = 4.0d;
    private static final double RANGE_LINE_INSET = 0.03125d;
    private static final float RANGE_LINE_RED = 0.2f;
    private static final float RANGE_LINE_GREEN = 0.85f;
    private static final float RANGE_LINE_BLUE = 1.0f;
    private static final float RANGE_LINE_ALPHA = 0.5f;
    private static final ResourceLocation CORE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Data_Energistics.MODID, "textures/block/data_distribution_tower_core.png");

    public DataDistributionTowerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DataDistributionTowerBlockEntity blockEntity) {
        return false;
    }

    @Override
    public void render(@NotNull DataDistributionTowerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderAnimatedCore(blockEntity, partialTick, poseStack);

        if (blockEntity.isRangeDisplayEnabled()) {
            AABB aabb = blockEntity.getCoverageAabb().move(
                    -blockEntity.getBlockPos().getX(),
                    -blockEntity.getBlockPos().getY(),
                    -blockEntity.getBlockPos().getZ()
            ).deflate(RANGE_LINE_INSET);

            var consumer = buffer.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(poseStack, consumer, aabb, RANGE_LINE_RED, RANGE_LINE_GREEN, RANGE_LINE_BLUE, RANGE_LINE_ALPHA);
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull DataDistributionTowerBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).expandTowards(0.0d, RENDER_BOX_HEIGHT, 0.0d);
    }

    private void renderAnimatedCore(DataDistributionTowerBlockEntity blockEntity, float partialTick, PoseStack poseStack) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        boolean online = blockEntity.isNetworkNodeOnline();
        float time = blockEntity.getLevel().getGameTime() + partialTick;
        float bobOffset = Mth.sin(time * 0.08f) * 0.08f;
        float outerRotation = online ? time * 4.0f : 0.0f;
        float tiltRotation = online ? 14.0f + Mth.sin(time * 0.05f) * 6.0f : 14.0f;

        poseStack.pushPose();
        poseStack.translate(0.5f, CORE_BASE_Y + bobOffset, 0.5f);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(outerRotation * 1.5f));
        poseStack.mulPose(Axis.XP.rotationDegrees(tiltRotation));
        int fillRed = online ? ONLINE_FILL_RED : OFFLINE_FILL_RED;
        int fillGreen = online ? ONLINE_FILL_GREEN : OFFLINE_FILL_GREEN;
        int fillBlue = online ? ONLINE_FILL_BLUE : OFFLINE_FILL_BLUE;
        int textureRed = online ? ONLINE_TEXTURE_TINT : OFFLINE_TEXTURE_RED;
        int textureGreen = online ? ONLINE_TEXTURE_TINT : OFFLINE_TEXTURE_GREEN;
        int textureBlue = online ? ONLINE_TEXTURE_TINT : OFFLINE_TEXTURE_BLUE;
        renderSolidCube(poseStack, CORE_FILL_SIZE, fillRed, fillGreen, fillBlue, 255);
        renderTexturedCube(poseStack, CORE_SIZE, textureRed, textureGreen, textureBlue, 255);
        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderSolidCube(PoseStack poseStack, float size,
                                 int red, int green, int blue, int alpha) {
        float half = size * 0.5f;
        Matrix4f matrix = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        addSolidQuad(builder, matrix, -half, -half, half, half, -half, half, half, half, half, -half, half, half, red, green, blue, alpha);
        addSolidQuad(builder, matrix, half, -half, -half, -half, -half, -half, -half, half, -half, half, half, -half, red, green, blue, alpha);
        addSolidQuad(builder, matrix, -half, -half, -half, -half, -half, half, -half, half, half, -half, half, -half, red, green, blue, alpha);
        addSolidQuad(builder, matrix, half, -half, half, half, -half, -half, half, half, -half, half, half, half, red, green, blue, alpha);
        addSolidQuad(builder, matrix, -half, half, half, half, half, half, half, half, -half, -half, half, -half, red, green, blue, alpha);
        addSolidQuad(builder, matrix, -half, -half, -half, half, -half, -half, half, -half, half, -half, -half, half, red, green, blue, alpha);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableCull();
    }

    private void renderTexturedCube(PoseStack poseStack, float size,
                                    int red, int green, int blue, int alpha) {
        float half = size * 0.5f;
        Matrix4f matrix = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, CORE_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        addQuad(builder, matrix, -half, -half, half, half, -half, half, half, half, half, -half, half, half, red, green, blue, alpha);
        addQuad(builder, matrix, half, -half, -half, -half, -half, -half, -half, half, -half, half, half, -half, red, green, blue, alpha);
        addQuad(builder, matrix, -half, -half, -half, -half, -half, half, -half, half, half, -half, half, -half, red, green, blue, alpha);
        addQuad(builder, matrix, half, -half, half, half, -half, -half, half, half, -half, half, half, half, red, green, blue, alpha);
        addQuad(builder, matrix, -half, half, half, half, half, half, half, half, -half, -half, half, -half, red, green, blue, alpha);
        addQuad(builder, matrix, -half, -half, -half, half, -half, -half, half, -half, half, -half, -half, half, red, green, blue, alpha);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
    }

    private void addSolidQuad(BufferBuilder builder, Matrix4f matrix,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              float x3, float y3, float z3,
                              float x4, float y4, float z4,
                              int red, int green, int blue, int alpha) {
        builder.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, x3, y3, z3).setColor(red, green, blue, alpha);
        builder.addVertex(matrix, x4, y4, z4).setColor(red, green, blue, alpha);
    }

    private void addQuad(BufferBuilder builder, Matrix4f matrix,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4,
                         int red, int green, int blue, int alpha) {
        builder.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setUv(0.0f, 1.0f);
        builder.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setUv(1.0f, 1.0f);
        builder.addVertex(matrix, x3, y3, z3).setColor(red, green, blue, alpha).setUv(1.0f, 0.0f);
        builder.addVertex(matrix, x4, y4, z4).setColor(red, green, blue, alpha).setUv(0.0f, 0.0f);
    }
}
