package com.fish_dan_.data_energistics.mixin;

import java.util.List;

import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "appeng.crafting.pattern.EncodedPatternItem")
public abstract class EncodedPatternItemMixin {
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void dataEnergistics$appendPatternSourceTooltip(ItemStack stack,
                                                            net.minecraft.world.item.Item.TooltipContext context,
                                                            List<Component> lines,
                                                            TooltipFlag flags,
                                                            CallbackInfo ci) {
        PatternEncodingSourceHelper.appendPatternSourceTooltip(stack, lines);
    }
}
