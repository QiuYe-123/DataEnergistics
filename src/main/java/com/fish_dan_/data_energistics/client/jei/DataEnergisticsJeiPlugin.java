package com.fish_dan_.data_energistics.client.jei;

import appeng.client.gui.AEBaseScreen;
import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.client.screen.UniversalTerminalScreenHook;
import com.fish_dan_.data_energistics.registry.ModItems;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import com.fish_dan_.data_energistics.util.UniversalTerminalData;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

@JeiPlugin
public final class DataEnergisticsJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Data_Energistics.MODID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AEBaseScreen.class, UniversalTerminalGuiHandler.INSTANCE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TimeShiftCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        List<RecipeHolder<com.fish_dan_.data_energistics.recipe.TimeShiftRecipe>> recipes =
                minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipes.TIME_SHIFT_TYPE.get());
        registration.addRecipes(TimeShiftCategory.RECIPE_TYPE, recipes);
        registration.addRecipes(RecipeTypes.CRAFTING, buildUniversalTerminalRecipes(minecraft.level.registryAccess()));
    }

    private static List<RecipeHolder<CraftingRecipe>> buildUniversalTerminalRecipes(net.minecraft.core.HolderLookup.Provider registries) {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        List<UniversalTerminalData.TerminalEntry> terminals = UniversalTerminalData.getDefinitions().stream()
                .filter(definition -> !new ItemStack(ModItems.UNIVERSAL_TERMINAL.get()).is(definition.createIcon().getItem()))
                .map(definition -> {
                    ItemStack stack = definition.createIcon();
                    return stack.isEmpty() ? null : new UniversalTerminalData.TerminalEntry(definition.name(), stack);
                })
                .filter(Objects::nonNull)
                .toList();

        for (int i = 0; i < terminals.size(); i++) {
            for (int j = i + 1; j < terminals.size(); j++) {
                UniversalTerminalData.TerminalEntry first = terminals.get(i);
                UniversalTerminalData.TerminalEntry second = terminals.get(j);
                recipes.add(new RecipeHolder<>(
                        Data_Energistics.id("universal_terminal_combine/" + sanitize(first.name()) + "_" + sanitize(second.name())),
                        new ShapelessRecipe(
                                "",
                                CraftingBookCategory.MISC,
                                new ItemStack(ModItems.UNIVERSAL_TERMINAL.get()),
                                NonNullList.of(
                                        Ingredient.EMPTY,
                                        Ingredient.of(first.stack().copy()),
                                        Ingredient.of(second.stack().copy())
                                )
                        )
                ));
            }
        }

        return recipes;
    }

    private static String sanitize(String terminalName) {
        return terminalName.replace(':', '_').replace('/', '_');
    }

    private static final class UniversalTerminalGuiHandler implements IGuiContainerHandler<AEBaseScreen<?>> {
        private static final UniversalTerminalGuiHandler INSTANCE = new UniversalTerminalGuiHandler();

        @Override
        public List<Rect2i> getGuiExtraAreas(AEBaseScreen<?> containerScreen) {
            var selectorPanel = UniversalTerminalScreenHook.getSelectorPanel(containerScreen);
            if (selectorPanel == null || !selectorPanel.isOpen()) {
                return List.of();
            }

            return List.of(selectorPanel.getExclusionArea());
        }
    }
}
