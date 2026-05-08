package com.fish_dan_.data_energistics.mixin;

import cn.dancingsnow.neoecoae.api.rendering.FixedBlockEntityRenderers;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Pseudo
@Mixin(targets = "cn.dancingsnow.neoecoae.client.NeoECOAEClient", remap = false)
public class NeoECOAEClientMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_WARNINGS = 5;
    private static final AtomicInteger SAFE_RENDER_FAILURES = new AtomicInteger();

    @Inject(method = "onAddChunkGeometry", at = @At("HEAD"), cancellable = true)
    private static void dataEnergistics$wrapAddChunkGeometry(AddSectionGeometryEvent event, CallbackInfo ci) {
        BlockPos sectionOrigin = event.getSectionOrigin();
        event.addRenderer(context -> {
            try {
                FixedBlockEntityRenderers.render(context, sectionOrigin);
            } catch (RuntimeException exception) {
                int failureCount = SAFE_RENDER_FAILURES.incrementAndGet();
                if (failureCount <= MAX_WARNINGS) {
                    LOGGER.warn(
                            "Skipped NeoECOAE additional chunk geometry at {} after renderer failure #{}",
                            sectionOrigin,
                            failureCount,
                            exception);
                }
            }
        });
        ci.cancel();
    }
}
