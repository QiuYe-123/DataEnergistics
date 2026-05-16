package com.fish_dan_.data_energistics.client.jei;

import com.fish_dan_.data_energistics.recipe.TimeShiftIngredient;
import com.fish_dan_.data_energistics.recipe.TimeShiftRecipe;
import com.fish_dan_.data_energistics.registry.ModItems;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class TimeShiftRecipeCategory extends AbstractRecipeCategory<TimeShiftRecipe> {
    private static final int WIDTH = 140;
    private static final int HEIGHT = 80;
    private static final int CENTER_Y = 40;
    private static final int SLOT_SIZE = 18;
    private static final int INPUT_X = 16;
    private static final int ARROW_X = 58;
    private static final int OUTPUT_X = 106;
    private static final int TEXT_X = 35;
    private static final int TEXT_WIDTH = 70;
    private static final int TEXT_COLOR = 0x7E7E7E;

    public static final RecipeType<TimeShiftRecipe> RECIPE_TYPE =
            RecipeType.create("data_energistics", "time_shift", TimeShiftRecipe.class);

    private final IDrawable background;
    private final IDrawableStatic slotDrawable;
    private final IDrawableStatic recipeArrow;

    public TimeShiftRecipeCategory(IGuiHelper guiHelper) {
        super(
                RECIPE_TYPE,
                Component.translatable("recipe.data_energistics.time_shift.category"),
                guiHelper.createDrawableItemLike(ModItems.DATA_CRYSTAL.get()),
                WIDTH,
                HEIGHT);
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.recipeArrow = guiHelper.getRecipeArrow();
    }

    @Override
    public void draw(TimeShiftRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        drawSlotFrames(guiGraphics, recipe);

        Font font = Minecraft.getInstance().font;
        drawCenteredString(
                guiGraphics,
                font,
                Component.translatable("recipe.data_energistics.time_shift"),
                TEXT_X + TEXT_WIDTH / 2,
                8);
        this.recipeArrow.draw(guiGraphics, ARROW_X, CENTER_Y - 8);

        Component conditionText = Component.translatable(
                "recipe.data_energistics.time_shift.time." + recipe.getTimeCondition().getName());
        Component timeText = Component.translatable(
                "recipe.data_energistics.time_shift.duration",
                formatMinutes(recipe),
                conditionText);
        drawCenteredString(guiGraphics, font, timeText, TEXT_X + TEXT_WIDTH / 2, CENTER_Y + 16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TimeShiftRecipe recipe, IFocusGroup focuses) {
        int inputRows = visibleRows(recipe.getItemInputs().size());
        int inputStartY = centeredSlotY(recipe.getItemInputs().size());
        for (int i = 0; i < recipe.getItemInputs().size(); i++) {
            int x = INPUT_X + i / inputRows * SLOT_SIZE;
            int y = inputStartY + i % inputRows * SLOT_SIZE;
            builder.addInputSlot(x, y).addItemStacks(withCount(recipe.getItemInputs().get(i)));
        }

        int outputRows = visibleRows(recipe.getResults().size());
        int outputStartY = centeredSlotY(recipe.getResults().size());
        for (int i = 0; i < recipe.getResults().size(); i++) {
            int x = OUTPUT_X + i / outputRows * SLOT_SIZE;
            int y = outputStartY + i % outputRows * SLOT_SIZE;
            builder.addOutputSlot(x, y).addItemStack(recipe.getResults().get(i).copy());
        }
    }

    private void drawSlotFrames(GuiGraphics guiGraphics, TimeShiftRecipe recipe) {
        int inputRows = visibleRows(recipe.getItemInputs().size());
        int inputStartY = centeredSlotY(recipe.getItemInputs().size());
        for (int i = 0; i < recipe.getItemInputs().size(); i++) {
            int x = INPUT_X + i / inputRows * SLOT_SIZE;
            int y = inputStartY + i % inputRows * SLOT_SIZE;
            this.slotDrawable.draw(guiGraphics, x - 1, y - 1);
        }

        int outputRows = visibleRows(recipe.getResults().size());
        int outputStartY = centeredSlotY(recipe.getResults().size());
        for (int i = 0; i < recipe.getResults().size(); i++) {
            int x = OUTPUT_X + i / outputRows * SLOT_SIZE;
            int y = outputStartY + i % outputRows * SLOT_SIZE;
            this.slotDrawable.draw(guiGraphics, x - 1, y - 1);
        }
    }

    private static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int centerX, int y) {
        guiGraphics.drawString(font, text, centerX - font.width(text) / 2, y, TEXT_COLOR, false);
    }

    private static String formatMinutes(TimeShiftRecipe recipe) {
        double minutes = recipe.getDurationMinutes();
        if (minutes == Math.rint(minutes)) {
            return Integer.toString((int) minutes);
        }

        return String.format(Locale.ROOT, "%.2f", minutes);
    }

    private static int centeredSlotY(int slotCount) {
        return CENTER_Y - visibleRows(slotCount) * SLOT_SIZE / 2;
    }

    private static int visibleRows(int slotCount) {
        return Math.min(Math.max(slotCount, 1), 3);
    }

    private static List<ItemStack> withCount(TimeShiftIngredient ingredient) {
        return Arrays.stream(ingredient.ingredient().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(ingredient.count());
            return copy;
        }).toList();
    }
}
