package com.fish_dan_.data_energistics.client.jei;

import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.menu.universal.UniversalCraftingTermMenu;
import com.fish_dan_.data_energistics.menu.universal.UniversalPatternEncodingTermMenu;
import com.fish_dan_.data_energistics.registry.ModMenus;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;

@JeiPlugin
public final class DataEnergisticsJeiPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String CRAFTING_HANDLER_CLASS =
            "tamaized.ae2jeiintegration.integration.modules.jei.transfer.UseCraftingRecipeTransfer";
    private static final String ENCODING_HANDLER_CLASS =
            "tamaized.ae2jeiintegration.integration.modules.jei.transfer.EncodePatternTransferHandler";

    @Override
    public ResourceLocation getPluginUid() {
        return Data_Energistics.id("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        var craftingHandler = createCraftingHandler(registration.getTransferHelper());
        if (craftingHandler != null) {
            registration.addRecipeTransferHandler(craftingHandler, RecipeTypes.CRAFTING);
        }

        var encodingHandler = createEncodingHandler(
                registration.getTransferHelper(),
                registration.getJeiHelpers().getIngredientVisibility());
        if (encodingHandler != null) {
            registration.addUniversalRecipeTransferHandler(encodingHandler);
        }
    }

    @SuppressWarnings("unchecked")
    private static IRecipeTransferHandler<UniversalCraftingTermMenu, net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>>
    createCraftingHandler(IRecipeTransferHandlerHelper transferHelper) {
        try {
            Class<?> handlerClass = Class.forName(CRAFTING_HANDLER_CLASS);
            Constructor<?> constructor = handlerClass.getConstructor(
                    Class.class,
                    net.minecraft.world.inventory.MenuType.class,
                    IRecipeTransferHandlerHelper.class);
            return (IRecipeTransferHandler<UniversalCraftingTermMenu, net.minecraft.world.item.crafting.RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>>) constructor.newInstance(
                    UniversalCraftingTermMenu.class,
                    ModMenus.UNIVERSAL_CRAFTING_TERM.get(),
                    transferHelper);
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.warn("Failed to register JEI crafting transfer for universal crafting terminal", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static IUniversalRecipeTransferHandler<UniversalPatternEncodingTermMenu> createEncodingHandler(
            IRecipeTransferHandlerHelper transferHelper,
            mezz.jei.api.runtime.IIngredientVisibility ingredientVisibility) {
        try {
            Class<?> handlerClass = Class.forName(ENCODING_HANDLER_CLASS);
            Constructor<?> constructor = handlerClass.getConstructor(
                    net.minecraft.world.inventory.MenuType.class,
                    Class.class,
                    IRecipeTransferHandlerHelper.class,
                    mezz.jei.api.runtime.IIngredientVisibility.class);
            return (IUniversalRecipeTransferHandler<UniversalPatternEncodingTermMenu>) constructor.newInstance(
                    ModMenus.UNIVERSAL_PATTERN_ENCODING_TERM.get(),
                    UniversalPatternEncodingTermMenu.class,
                    transferHelper,
                    ingredientVisibility);
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.warn("Failed to register JEI transfer for universal pattern encoding terminal", e);
            return null;
        }
    }
}
