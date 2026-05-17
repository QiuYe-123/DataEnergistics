package com.fish_dan_.data_energistics.recipe;

import com.fish_dan_.data_energistics.item.DataCaptureBallItem;
import com.fish_dan_.data_energistics.item.MatterConvergingCrossbowItem;
import com.fish_dan_.data_energistics.registry.ModItems;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DataCaptureBallToCrossbowRecipe extends CustomRecipe {
    public DataCaptureBallToCrossbowRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input, level.registryAccess()).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int ballSlot = -1;
        int crossbowSlot = -1;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.DATA_CAPTURE_BALL.get())) {
                if (ballSlot != -1) {
                    return ItemStack.EMPTY;
                }
                ballSlot = i;
                continue;
            }

            if (stack.is(ModItems.MATTER_CONVERGING_CROSSBOW.get())) {
                if (crossbowSlot != -1) {
                    return ItemStack.EMPTY;
                }
                crossbowSlot = i;
                continue;
            }

            return ItemStack.EMPTY;
        }

        if (ballSlot == -1 || crossbowSlot == -1) {
            return ItemStack.EMPTY;
        }

        ItemStack ballStack = input.getItem(ballSlot);
        ItemStack crossbowStack = input.getItem(crossbowSlot).copy();
        long storedData = DataCaptureBallItem.getStoredDataAmount(ballStack);
        if (storedData <= 0L) {
            return ItemStack.EMPTY;
        }

        if (!(crossbowStack.getItem() instanceof MatterConvergingCrossbowItem crossbowItem)) {
            return ItemStack.EMPTY;
        }

        long accepted = crossbowItem.insertStoredData(crossbowStack, storedData);
        if (accepted != storedData) {
            return ItemStack.EMPTY;
        }

        return crossbowStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.is(ModItems.DATA_CAPTURE_BALL.get())) {
                continue;
            }

            ItemStack returnedBall = stack.copy();
            if (returnedBall.getItem() instanceof DataCaptureBallItem ballItem) {
                ballItem.clearStoredData(returnedBall);
            }
            remaining.set(i, returnedBall);
            break;
        }

        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DATA_CAPTURE_BALL_TO_CROSSBOW_SERIALIZER.get();
    }
}
