package com.fish_dan_.data_energistics.recipe;

import appeng.api.stacks.GenericStack;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.Nullable;

public record DataRipperReassemblerRecipeInput(List<ItemStack> items, List<GenericStack> fluidInputs,
                                               @Nullable GenericStack keyInput) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }
}
