package com.fish_dan_.data_energistics.mixin;

import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import appeng.menu.me.items.CraftingTermMenu;
import com.fish_dan_.data_energistics.item.PoweredEnergyItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(CraftingTermMenu.class)
public abstract class CraftingTermMenuMixin {
    @Shadow(remap = false)
    @Final
    private CraftingTermSlot outputSlot;

    @Shadow(remap = false)
    @Final
    private CraftingMatrixSlot[] craftingSlots;

    @Inject(method = "updateCurrentRecipeAndOutput", at = @At("TAIL"), remap = false)
    private void dataEnergistics$clearTerminalCraftResultWithoutEnergy(boolean forceUpdate, CallbackInfo ci) {
        ArrayList<ItemStack> testItems = new ArrayList<>(this.craftingSlots.length);
        for (CraftingMatrixSlot craftingSlot : this.craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }

        CraftingInput input = CraftingInput.of(3, 3, testItems);
        if (!PoweredEnergyItem.canCraftWithEnergy(input)) {
            this.outputSlot.set(ItemStack.EMPTY);
        }
    }
}
