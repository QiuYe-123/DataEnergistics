package com.fish_dan_.data_energistics.client.jei;

import appeng.core.AppEng;
import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.GenericStack;
import com.fish_dan_.data_energistics.client.GenericStackDisplayHelper;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerIngredient;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerRecipe;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class DataRipperReassemblerRecipeCategory extends AbstractRecipeCategory<DataRipperReassemblerRecipe> {
    private static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/data_reassembler.png");
    private static final ResourceLocation PROGRESS_TEXTURE = AppEng.makeId("textures/guis/crystal_assembler.png");
    private static final int ITEM_INPUT_START_X = 7;
    private static final int ITEM_INPUT_START_Y = 3;
    private static final int SLOT_SPACING = 18;
    private static final int KEY_INPUT_X = 62;
    private static final int KEY_INPUT_Y = 20;
    private static final int FLUID_INPUT_A_X = 62;
    private static final int FLUID_INPUT_A_Y = 2;
    private static final int FLUID_INPUT_B_X = 62;
    private static final int FLUID_INPUT_B_Y = 38;
    private static final int FLUID_OUTPUT_A_X = 132;
    private static final int FLUID_OUTPUT_A_Y = 39;
    private static final int FLUID_OUTPUT_B_X = 132;
    private static final int FLUID_OUTPUT_B_Y = 3;
    private static final int[][] OUTPUT_POSITIONS = {
            {113, 2},
            {113, 20},
            {113, 38}
    };
    public static final RecipeType<DataRipperReassemblerRecipe> RECIPE_TYPE =
            RecipeType.create("data_energistics", "data_reassembler", DataRipperReassemblerRecipe.class);

    private final IDrawable background;
    private final IDrawableAnimated progress;

    public DataRipperReassemblerRecipeCategory(IGuiHelper guiHelper) {
        super(
                RECIPE_TYPE,
                Component.translatable("recipe.data_energistics.data_reassembler"),
                guiHelper.createDrawableItemLike(ModBlocks.DATA_RIPPER_REASSEMBLER.get()),
                162,
                58);
        this.background = guiHelper.createDrawable(TEXTURE, 11, 19, 162, 58);
        this.progress = guiHelper.drawableBuilder(PROGRESS_TEXTURE, 176, 0, 6, 18)
                .buildAnimated(40, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public void draw(DataRipperReassemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        this.progress.draw(guiGraphics, 153, 20);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DataRipperReassemblerRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.getItemInputs().size(); i++) {
            DataRipperReassemblerIngredient ingredient = recipe.getItemInputs().get(i);
            int x = ITEM_INPUT_START_X + i % 3 * SLOT_SPACING;
            int y = ITEM_INPUT_START_Y + i / 3 * SLOT_SPACING;
            builder.addInputSlot(x, y).addItemStacks(withCount(ingredient));
        }

        for (int i = 0; i < recipe.getItemOutputs().size() && i < OUTPUT_POSITIONS.length; i++) {
            int[] pos = OUTPUT_POSITIONS[i];
            builder.addOutputSlot(pos[0], pos[1]).addItemStack(recipe.getItemOutputs().get(i).copy());
        }

        addGenericStackInputs(builder, recipe.getFluidInputs(),
                new int[][]{{FLUID_INPUT_A_X, FLUID_INPUT_A_Y}, {FLUID_INPUT_B_X, FLUID_INPUT_B_Y}});
        addGenericStackOutputs(builder, recipe.getFluidOutputs(),
                new int[][]{{FLUID_OUTPUT_A_X, FLUID_OUTPUT_A_Y}, {FLUID_OUTPUT_B_X, FLUID_OUTPUT_B_Y}});

        GenericStack keyInput = recipe.getKeyInput();
        if (keyInput != null) {
            builder.addInputSlot(KEY_INPUT_X, KEY_INPUT_Y)
                    .addItemStack(keyInput.what().wrapForDisplayOrFilter())
                    .setCustomRenderer(VanillaTypes.ITEM_STACK, new NoCountItemRenderer(keyInput))
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        tooltip.add(createKeyTooltip(keyInput));
                        tooltip.add(GenericStackDisplayHelper.createAmountTooltip(keyInput));
                    });
        }
    }

    private static List<ItemStack> withCount(DataRipperReassemblerIngredient ingredient) {
        return Arrays.stream(ingredient.ingredient().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(ingredient.count());
            return copy;
        }).toList();
    }

    private static Component createKeyTooltip(GenericStack keyInput) {
        return Component.empty()
                .append(keyInput.what().getDisplayName());
    }

    private static void addGenericStackInputs(IRecipeLayoutBuilder builder, List<GenericStack> stacks, int[][] positions) {
        for (int i = 0; i < stacks.size() && i < positions.length; i++) {
            GenericStack stack = stacks.get(i);
            int[] pos = positions[i];
            builder.addInputSlot(pos[0], pos[1])
                    .addItemStack(stack.what().wrapForDisplayOrFilter())
                    .setCustomRenderer(VanillaTypes.ITEM_STACK, new NoCountItemRenderer(stack))
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        tooltip.add(createKeyTooltip(stack));
                        tooltip.add(GenericStackDisplayHelper.createAmountTooltip(stack));
                    });
        }
    }

    private static void addGenericStackOutputs(IRecipeLayoutBuilder builder, List<GenericStack> stacks, int[][] positions) {
        for (int i = 0; i < stacks.size() && i < positions.length; i++) {
            GenericStack stack = stacks.get(i);
            int[] pos = positions[i];
            builder.addOutputSlot(pos[0], pos[1])
                    .addItemStack(stack.what().wrapForDisplayOrFilter())
                    .setCustomRenderer(VanillaTypes.ITEM_STACK, new NoCountItemRenderer(stack))
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        tooltip.add(createKeyTooltip(stack));
                        tooltip.add(GenericStackDisplayHelper.createAmountTooltip(stack));
                    });
        }
    }

    private record NoCountItemRenderer(GenericStack keyInput) implements IIngredientRenderer<ItemStack> {

        @Override
        public void render(GuiGraphics guiGraphics, ItemStack ingredient) {
            AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, 0, 0, keyInput.what());
            GenericStackDisplayHelper.renderSmallOverlay(
                    guiGraphics,
                    0,
                    0,
                    GenericStackDisplayHelper.formatCompactAmount(keyInput));
        }

        @Override
        public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
            return ingredient.getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, tooltipFlag);
        }

        @Override
        public Font getFontRenderer(Minecraft minecraft, ItemStack ingredient) {
            return minecraft.font;
        }
    }
}
