package com.fish_dan_.data_energistics.client.render;

import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.blockentity.DataDistributionTowerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class DataDistributionTowerRenderer implements BlockEntityRenderer<DataDistributionTowerBlockEntity> {
    private static final float CRYSTAL_BASE_Y = 2.8125f;
    private static final double RENDER_BOX_HEIGHT = 4.0d;
    private static final double RANGE_LINE_INSET = 0.03125d;
    private static final float RANGE_LINE_RED = 0.2f;
    private static final float RANGE_LINE_GREEN = 0.85f;
    private static final float RANGE_LINE_BLUE = 1.0f;
    private static final float RANGE_LINE_ALPHA = 0.5f;
    private static final ModelResourceLocation CRYSTAL_OFFLINE_MODEL =
            ModelResourceLocation.standalone(Data_Energistics.id("block/data_distribution_tower_crystal_offline"));
    private static final ModelResourceLocation CRYSTAL_ONLINE_MODEL =
            ModelResourceLocation.standalone(Data_Energistics.id("block/data_distribution_tower_crystal_online"));

    @SuppressWarnings("unused")
    public DataDistributionTowerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DataDistributionTowerBlockEntity blockEntity) {
        return false;
    }

    @Override
    public void render(@NotNull DataDistributionTowerBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderCrystal(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);

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

    private void renderCrystal(DataDistributionTowerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        boolean online = blockEntity.isNetworkNodeOnline();
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BakedModel model = minecraft.getModelManager().getModel(online ? CRYSTAL_ONLINE_MODEL : CRYSTAL_OFFLINE_MODEL);
        BlockState state = blockEntity.getBlockState();
        float bobOffset = online ? Mth.sin((blockEntity.getLevel().getGameTime() + partialTick) * 0.08f) * 0.08f : 0.0f;

        poseStack.pushPose();
        poseStack.translate(0.5f, CRYSTAL_BASE_Y + bobOffset, 0.5f);
        poseStack.translate(-0.5f, -1.75f, -0.5f);
        renderModel(blockRenderer, model, state, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static void renderModel(BlockRenderDispatcher blockRenderer, BakedModel model, BlockState state, PoseStack poseStack,
                                    MultiBufferSource buffer, int packedLight, int packedOverlay) {
        RandomSource random = RandomSource.create(42L);
        for (RenderType renderType : model.getRenderTypes(state, random, ModelData.EMPTY)) {
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false));
            blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(),
                    consumer,
                    state,
                    model,
                    1.0f,
                    1.0f,
                    1.0f,
                    packedLight,
                    packedOverlay,
                    ModelData.EMPTY,
                    renderType
            );
        }
    }
}
