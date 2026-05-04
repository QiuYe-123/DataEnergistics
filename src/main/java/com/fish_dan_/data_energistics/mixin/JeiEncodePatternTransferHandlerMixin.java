package com.fish_dan_.data_energistics.mixin;

import com.fish_dan_.data_energistics.util.PatternEncodingSourceHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "tamaized.ae2jeiintegration.integration.modules.jei.transfer.EncodePatternTransferHandler", remap = false)
public abstract class JeiEncodePatternTransferHandlerMixin {
    @Inject(
            method = "transferRecipe(Lnet/minecraft/world/inventory/AbstractContainerMenu;Ljava/lang/Object;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/world/entity/player/Player;ZZ)Lmezz/jei/api/recipe/transfer/IRecipeTransferError;",
            at = @At("RETURN"))
    private void dataEnergistics$rememberPatternSource(Object menu, Object recipe,
                                                       Object recipeSlots, Object player,
                                                       boolean maxTransfer, boolean doTransfer,
                                                       CallbackInfoReturnable<Object> cir) {
        if (!doTransfer || cir.getReturnValue() != null) {
            return;
        }

        if (menu instanceof appeng.menu.me.items.PatternEncodingTermMenu patternEncodingTermMenu) {
            PatternEncodingSourceHelper.rememberTransferSource(patternEncodingTermMenu, recipe);
        }
    }
}
