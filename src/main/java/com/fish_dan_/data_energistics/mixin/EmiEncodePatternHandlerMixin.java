package com.fish_dan_.data_energistics.mixin;

import appeng.menu.me.items.PatternEncodingTermMenu;
import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng.integration.modules.emi.EmiEncodePatternHandler", remap = false)
public abstract class EmiEncodePatternHandlerMixin {
    @Inject(
            method = "transferRecipe(Lappeng/menu/me/items/PatternEncodingTermMenu;Lnet/minecraft/world/item/crafting/RecipeHolder;Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At("RETURN"))
    private void dataEnergistics$rememberPatternSource(PatternEncodingTermMenu menu, RecipeHolder<?> holder,
                                                       EmiRecipe emiRecipe, boolean doTransfer,
                                                       CallbackInfoReturnable<Object> cir) {
        if (!doTransfer || cir.getReturnValue() == null) {
            return;
        }

        if (!cir.getReturnValue().getClass().getName().endsWith("$Success")) {
            return;
        }

        PatternEncodingSourceHelper.rememberTransferSource(menu, holder, emiRecipe);
    }
}
