package com.fish_dan_.data_energistics.client.emi;

import appeng.core.AppEng;
import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.GenericStack;
import com.fish_dan_.data_energistics.client.GenericStackDisplayHelper;
import com.fish_dan_.data_energistics.client.DataReassemblerLayout;
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
        super(CATEGORY, holder.id(), DataReassemblerLayout.RECIPE_WIDTH, DataReassemblerLayout.RECIPE_HEIGHT);
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
        widgets.addTexture(BACKGROUND, 0, 0,
                DataReassemblerLayout.RECIPE_WIDTH, DataReassemblerLayout.RECIPE_HEIGHT, 11, 19);
        widgets.addAnimatedTexture(PROGRESS_TEXTURE,
                DataReassemblerLayout.PROGRESS_X, DataReassemblerLayout.PROGRESS_Y,
                DataReassemblerLayout.PROGRESS_WIDTH, DataReassemblerLayout.PROGRESS_HEIGHT,
                176, 0, 2000, false, true, false);

        for (int i = 0; i < this.inputs.size(); i++) {
            var pos = DataReassemblerLayout.emiItemInput(i);
            widgets.addSlot(this.inputs.get(i), pos.x(), pos.y()).drawBack(false);
        }

        for (int i = 0; i < this.outputs.size() && i < DataRipperReassemblerRecipe.ITEM_OUTPUT_SLOTS; i++) {
            var pos = DataReassemblerLayout.emiItemOutput(i);
            widgets.addSlot(this.outputs.get(i), pos.x(), pos.y())
                    .recipeContext(this)
                    .drawBack(false);
        }

        addGenericStackWidgets(widgets, this.recipe.getFluidInputs(), true);
        addGenericStackWidgets(widgets, this.recipe.getFluidOutputs(), false);

        GenericStack keyInput = this.recipe.getKeyInput();
        if (keyInput != null) {
            var pos = DataReassemblerLayout.emiKeyInput();
            widgets.addDrawable(pos.x(), pos.y(), 18, 18,
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

    private void addGenericStackWidgets(WidgetHolder widgets, List<GenericStack> stacks, boolean input) {
        int slotCount = input ? DataRipperReassemblerRecipe.FLUID_INPUT_SLOTS : DataRipperReassemblerRecipe.FLUID_OUTPUT_SLOTS;
        for (int i = 0; i < stacks.size() && i < slotCount; i++) {
            GenericStack stack = stacks.get(i);
            var pos = input ? DataReassemblerLayout.emiFluidInput(i) : DataReassemblerLayout.emiFluidOutput(i);
            widgets.addDrawable(pos.x(), pos.y(), 18, 18,
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
