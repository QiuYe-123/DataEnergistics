package com.fish_dan_.data_energistics.client.jei;

import appeng.client.gui.AEBaseScreen;
import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.client.screen.UniversalTerminalScreenHook;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

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
