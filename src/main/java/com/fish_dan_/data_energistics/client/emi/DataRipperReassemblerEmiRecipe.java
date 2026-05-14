package com.fish_dan_.data_energistics.client.emi;

import appeng.core.AppEng;
import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.GenericStack;
import com.fish_dan_.data_energistics.client.GenericStackDisplayHelper;
import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerIngredient;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerRecipe;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class DataRipperReassemblerEmiRecipe extends BasicEmiRecipe {
    private static final ResourceLocation BACKGROUND = AppEng.makeId("textures/guis/data_reassembler.png");
    private static final ResourceLocation PROGRESS_TEXTURE = AppEng.makeId("textures/guis/crystal_assembler.png");
    private static final int WIDTH = 162;
    private static final int HEIGHT = 58;
    private static final int INPUT_START_X = 7;
    private static final int INPUT_START_Y = 2;
    private static final int SLOT_SIZE = 18;
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

    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            Data_Energistics.id("data_reassembler"),
            EmiStack.of(ModBlocks.DATA_RIPPER_REASSEMBLER.get())) {
        @Override
        public Component getName() {
            return Component.translatable("recipe.data_energistics.data_reassembler");
        }
    };

    private final DataRipperReassemblerRecipe recipe;

    public DataRipperReassemblerEmiRecipe(RecipeHolder<DataRipperReassemblerRecipe> holder) {
        super(CATEGORY, holder.id(), WIDTH, HEIGHT);
        this.recipe = holder.value();

        for (DataRipperReassemblerIngredient ingredient : this.recipe.getItemInputs()) {
            this.inputs.add(EmiIngredient.of(ingredient.ingredient(), ingredient.count()));
        }
        for (ItemStack output : this.recipe.getItemOutputs()) {
            this.outputs.add(EmiStack.of(output.copy()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, WIDTH, HEIGHT, 11, 19);
        widgets.addAnimatedTexture(PROGRESS_TEXTURE, 153, 20, 6, 18, 176, 0, 2000, false, true, false);

        for (int i = 0; i < this.inputs.size(); i++) {
            int x = INPUT_START_X + i % 3 * SLOT_SIZE;
            int y = INPUT_START_Y + i / 3 * SLOT_SIZE;
            widgets.addSlot(this.inputs.get(i), x, y).drawBack(false);
        }

        for (int i = 0; i < this.outputs.size() && i < OUTPUT_POSITIONS.length; i++) {
            int[] pos = OUTPUT_POSITIONS[i];
            widgets.addSlot(this.outputs.get(i), pos[0], pos[1])
                    .recipeContext(this)
                    .drawBack(false);
        }

        addGenericStackWidgets(widgets, this.recipe.getFluidInputs(),
                new int[][]{{FLUID_INPUT_A_X, FLUID_INPUT_A_Y}, {FLUID_INPUT_B_X, FLUID_INPUT_B_Y}});
        addGenericStackWidgets(widgets, this.recipe.getFluidOutputs(),
                new int[][]{{FLUID_OUTPUT_A_X, FLUID_OUTPUT_A_Y}, {FLUID_OUTPUT_B_X, FLUID_OUTPUT_B_Y}});

        GenericStack keyInput = this.recipe.getKeyInput();
        if (keyInput != null) {
            widgets.addDrawable(KEY_INPUT_X, KEY_INPUT_Y, 18, 18,
                            (guiGraphics, mouseX, mouseY, delta) -> {
                                AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, 1, 1, keyInput.what());
                                GenericStackDisplayHelper.renderSmallOverlay(
                                        guiGraphics,
                                        1,
                                        1,
                                        GenericStackDisplayHelper.formatCompactAmount(keyInput));
                            })
                    .tooltip((mouseX, mouseY) -> createEmiKeyTooltip(keyInput));
        }
    }

    private static Component createKeyTooltip(GenericStack keyInput) {
        return Component.empty()
                .append(keyInput.what().getDisplayName());
    }

    private List<ClientTooltipComponent> createEmiKeyTooltip(GenericStack keyInput) {
        return List.of(
                ClientTooltipComponent.create(createKeyTooltip(keyInput).getVisualOrderText()),
                ClientTooltipComponent.create(GenericStackDisplayHelper.createAmountTooltip(keyInput).getVisualOrderText()));
    }

    private void addGenericStackWidgets(WidgetHolder widgets, List<GenericStack> stacks, int[][] positions) {
        for (int i = 0; i < stacks.size() && i < positions.length; i++) {
            GenericStack stack = stacks.get(i);
            int[] pos = positions[i];
            widgets.addDrawable(pos[0], pos[1], 18, 18,
                            (guiGraphics, mouseX, mouseY, delta) -> {
                                AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, 1, 1, stack.what());
                                GenericStackDisplayHelper.renderSmallOverlay(
                                        guiGraphics,
                                        1,
                                        1,
                                        GenericStackDisplayHelper.formatCompactAmount(stack));
                            })
                    .tooltip((mouseX, mouseY) -> createEmiKeyTooltip(stack));
        }
    }
}
