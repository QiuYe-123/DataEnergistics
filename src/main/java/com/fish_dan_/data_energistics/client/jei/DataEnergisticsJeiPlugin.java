package com.fish_dan_.data_energistics.client.jei;

import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.menu.universal.UniversalCraftingTermMenu;
import com.fish_dan_.data_energistics.menu.universal.UniversalPatternEncodingTermMenu;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.registry.ModMenus;
import com.fish_dan_.data_energistics.registry.ModRecipes;
import com.mojang.logging.LogUtils;
import appeng.core.definitions.AEBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;
import com.fish_dan_.data_energistics.registry.ModItems;

import java.lang.reflect.Constructor;
import java.util.List;

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
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new TimeShiftRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new DataCaptureBallCondenserCategory(registration.getJeiHelpers().getGuiHelper()),
                new DataRipperReassemblerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEBlocks.CONDENSER, DataCaptureBallCondenserCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(ModBlocks.DATA_RIPPER_REASSEMBLER.get(), DataRipperReassemblerRecipeCategory.RECIPE_TYPE);
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

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(DataCaptureBallCondenserCategory.RECIPE_TYPE, List.of(DataCaptureBallCondenserRecipe.INSTANCE));
        var level = Minecraft.getInstance().level;
        if (level != null) {
            registration.addRecipes(
                    TimeShiftRecipeCategory.RECIPE_TYPE,
                    level.getRecipeManager().getAllRecipesFor(ModRecipes.TIME_SHIFT_TYPE.get()).stream()
                            .map(RecipeHolder::value)
                            .toList());
            registration.addRecipes(
                    DataRipperReassemblerRecipeCategory.RECIPE_TYPE,
                    level.getRecipeManager().getAllRecipesFor(ModRecipes.DATA_RIPPER_REASSEMBLER_TYPE.get()).stream()
                            .map(RecipeHolder::value)
                            .toList());
        }
        registerMatterConvergingCrossbowAnvilRecipes(registration);
        registration.addIngredientInfo(
                ModItems.RESIDUAL_DATA.get(),
                Component.translatable("jei.data_energistics.residual_data.line1"));
        registration.addIngredientInfo(
                ModItems.DEACTIVATED_REDSTONE_DUST.get(),
                Component.translatable("jei.data_energistics.deactivated_redstone_dust.line1"),
                Component.translatable("jei.data_energistics.deactivated_redstone_dust.line2"),
                Component.translatable("jei.data_energistics.deactivated_redstone_dust.line3"),
                Component.translatable("jei.data_energistics.deactivated_redstone_dust.line4"),
                Component.translatable("jei.data_energistics.deactivated_redstone_dust.line5"));
    }

    private static void registerMatterConvergingCrossbowAnvilRecipes(IRecipeRegistration registration) {
        HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> lookup =
                net.minecraft.client.Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var power = lookup.getOrThrow(Enchantments.POWER);

        ItemStack baseCrossbow = ModItems.MATTER_CONVERGING_CROSSBOW.get().getDefaultInstance();
        ItemStack enchantedCrossbow = baseCrossbow.copy();
        enchantedCrossbow.enchant(power, 1);

        ItemStack powerBook = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        builder.upgrade(power, 1);
        powerBook.set(DataComponents.STORED_ENCHANTMENTS, builder.toImmutable());

        registration.addRecipes(
                mezz.jei.api.constants.RecipeTypes.ANVIL,
                List.of(registration.getVanillaRecipeFactory().createAnvilRecipe(
                        baseCrossbow,
                        List.of(powerBook),
                        List.of(enchantedCrossbow),
                        Data_Energistics.id("anvil/matter_converging_crossbow_power"))));
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
