package com.fish_dan_.data_energistics.guideme;

import appeng.api.stacks.GenericStack;
import appeng.core.AppEng;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerIngredient;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerRecipe;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import guideme.compiler.tags.RecipeTypeMappingSupplier;
import guideme.document.block.recipes.LytStandardRecipeBox;
import guideme.document.interaction.GuideTooltip;
import guideme.render.RenderContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class DataRipperReassemblerGuideRecipeMappings implements RecipeTypeMappingSupplier {
    @Override
    public void collect(RecipeTypeMappings mappings) {
        mappings.add(
                ModRecipes.DATA_RIPPER_REASSEMBLER_TYPE.get(),
                DataRipperReassemblerGuideRecipeMappings::createRecipe);
    }

    private static LytStandardRecipeBox<DataRipperReassemblerRecipe> createRecipe(
            RecipeHolder<DataRipperReassemblerRecipe> holder) {
        var recipe = holder.value();

        return LytStandardRecipeBox.builder()
                .title("Data Reassembler")
                .icon(ModBlocks.DATA_RIPPER_REASSEMBLER.get())
                .customBody(new RecipeBody(recipe))
                .build(holder);
    }

    private static Ingredient withCount(DataRipperReassemblerIngredient ingredient) {
        return Ingredient.of(Arrays.stream(ingredient.ingredient().getItems()).map(stack -> {
            ItemStack copy = stack.copy();
            copy.setCount(ingredient.count());
            return copy;
        }));
    }

    private static String formatSeconds(int ticks) {
        double seconds = ticks / 20.0D;
        if (seconds == Math.rint(seconds)) {
            return Integer.toString((int) seconds);
        }
        return String.format(java.util.Locale.ROOT, "%.1f", seconds);
    }

    private static final class RecipeBody extends AbstractTexturedMachineGuideRecipeBody {
        private static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/data_reassembler.png");
        private static final ResourceLocation PROGRESS_TEXTURE = AppEng.makeId("textures/guis/crystal_assembler.png");
        private static final int WIDTH = 162;
        private static final int HEIGHT = 58;
        private static final int ITEM_INPUT_START_X = 7;
        private static final int ITEM_INPUT_START_Y = 3;
        private static final int SLOT_SPACING = 18;
        private static final int KEY_INPUT_X = 63;
        private static final int KEY_INPUT_Y = 21;
        private static final int FLUID_INPUT_A_X = 63;
        private static final int FLUID_INPUT_A_Y = 3;
        private static final int FLUID_INPUT_B_X = 63;
        private static final int FLUID_INPUT_B_Y = 39;
        private static final int FLUID_OUTPUT_A_X = 132;
        private static final int FLUID_OUTPUT_A_Y = 39;
        private static final int FLUID_OUTPUT_B_X = 132;
        private static final int FLUID_OUTPUT_B_Y = 3;
        private static final int PROGRESS_X = 153;
        private static final int PROGRESS_Y = 20;
        private static final int PROGRESS_WIDTH = 6;
        private static final int PROGRESS_HEIGHT = 18;
        private static final int[][] OUTPUT_POSITIONS = {
                {114, 2},
                {114, 20},
                {114, 38}
        };

        private final DataRipperReassemblerRecipe recipe;

        private RecipeBody(DataRipperReassemblerRecipe recipe) {
            super(TEXTURE, 11, 19, WIDTH, HEIGHT, PROGRESS_TEXTURE, PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT);
            this.recipe = recipe;
        }

        @Override
        protected void renderBody(RenderContext context) {
            renderItemInputs(context);
            renderItemOutputs(context);
            renderFluidInputs(context);
            renderFluidOutputs(context);
            renderKeyInput(context);
        }

        @Override
        protected List<Component> getProgressTooltipLines() {
            return List.of();
        }

        @Override
        protected Optional<GuideTooltip> getTooltipAt(float x, float y) {
            for (int i = 0; i < this.recipe.getItemInputs().size(); i++) {
                Optional<GuideTooltip> tooltip = getItemTooltipIfHovered(
                        x,
                        y,
                        getDisplayedInputStack(this.recipe.getItemInputs().get(i)),
                        ITEM_INPUT_START_X + i % 3 * SLOT_SPACING,
                        ITEM_INPUT_START_Y + i / 3 * SLOT_SPACING);
                if (tooltip.isPresent()) {
                    return tooltip;
                }
            }

            for (int i = 0; i < this.recipe.getItemOutputs().size() && i < OUTPUT_POSITIONS.length; i++) {
                int[] pos = OUTPUT_POSITIONS[i];
                Optional<GuideTooltip> tooltip = getItemTooltipIfHovered(
                        x, y, this.recipe.getItemOutputs().get(i).copy(), pos[0], pos[1]);
                if (tooltip.isPresent()) {
                    return tooltip;
                }
            }

            for (int i = 0; i < this.recipe.getFluidInputs().size() && i < 2; i++) {
                int[] pos = i == 0
                        ? new int[]{FLUID_INPUT_A_X, FLUID_INPUT_A_Y}
                        : new int[]{FLUID_INPUT_B_X, FLUID_INPUT_B_Y};
                Optional<GuideTooltip> tooltip = getGenericTooltipIfHovered(
                        x, y, this.recipe.getFluidInputs().get(i), pos[0], pos[1], List.of());
                if (tooltip.isPresent()) {
                    return tooltip;
                }
            }

            for (int i = 0; i < this.recipe.getFluidOutputs().size() && i < 2; i++) {
                int[] pos = i == 0
                        ? new int[]{FLUID_OUTPUT_A_X, FLUID_OUTPUT_A_Y}
                        : new int[]{FLUID_OUTPUT_B_X, FLUID_OUTPUT_B_Y};
                Optional<GuideTooltip> tooltip = getGenericTooltipIfHovered(
                        x, y, this.recipe.getFluidOutputs().get(i), pos[0], pos[1], List.of());
                if (tooltip.isPresent()) {
                    return tooltip;
                }
            }

            if (this.recipe.getKeyInput() != null) {
                return getGenericTooltipIfHovered(
                        x,
                        y,
                        this.recipe.getKeyInput(),
                        KEY_INPUT_X,
                        KEY_INPUT_Y,
                        List.of());
            }

            return Optional.empty();
        }

        private void renderItemInputs(RenderContext context) {
            for (int i = 0; i < this.recipe.getItemInputs().size(); i++) {
                renderItemStack(
                        context,
                        getDisplayedInputStack(this.recipe.getItemInputs().get(i)),
                        ITEM_INPUT_START_X + i % 3 * SLOT_SPACING,
                        ITEM_INPUT_START_Y + i / 3 * SLOT_SPACING);
            }
        }

        private void renderItemOutputs(RenderContext context) {
            for (int i = 0; i < this.recipe.getItemOutputs().size() && i < OUTPUT_POSITIONS.length; i++) {
                int[] pos = OUTPUT_POSITIONS[i];
                renderItemStack(context, this.recipe.getItemOutputs().get(i), pos[0], pos[1]);
            }
        }

        private void renderFluidInputs(RenderContext context) {
            for (int i = 0; i < this.recipe.getFluidInputs().size() && i < 2; i++) {
                int[] pos = i == 0
                        ? new int[]{FLUID_INPUT_A_X, FLUID_INPUT_A_Y}
                        : new int[]{FLUID_INPUT_B_X, FLUID_INPUT_B_Y};
                renderGenericStack(context, this.recipe.getFluidInputs().get(i), pos[0], pos[1]);
            }
        }

        private void renderFluidOutputs(RenderContext context) {
            for (int i = 0; i < this.recipe.getFluidOutputs().size() && i < 2; i++) {
                int[] pos = i == 0
                        ? new int[]{FLUID_OUTPUT_A_X, FLUID_OUTPUT_A_Y}
                        : new int[]{FLUID_OUTPUT_B_X, FLUID_OUTPUT_B_Y};
                renderGenericStack(context, this.recipe.getFluidOutputs().get(i), pos[0], pos[1]);
            }
        }

        private void renderKeyInput(RenderContext context) {
            if (this.recipe.getKeyInput() != null) {
                renderGenericStack(context, this.recipe.getKeyInput(), KEY_INPUT_X, KEY_INPUT_Y);
                renderGenericStackAmount(context, this.recipe.getKeyInput(), KEY_INPUT_X, KEY_INPUT_Y, formatCompactAmount(this.recipe.getKeyInput()));
            }
        }

        private ItemStack getDisplayedInputStack(DataRipperReassemblerIngredient ingredient) {
            return getDisplayedStack(withCount(ingredient).getItems());
        }
    }
}
