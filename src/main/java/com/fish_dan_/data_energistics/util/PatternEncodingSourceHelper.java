package com.fish_dan_.data_energistics.util;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.encoding.EncodingMode;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingSourceAware;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class PatternEncodingSourceHelper {
    public static final String ACTION_SET_PATTERN_SOURCE = "dataEnergisticsSetPatternSource";
    public static final String CLEAR_PATTERN_SOURCE = "";

    private static final String ROOT_TAG = "DataEnergisticsPatternSource";
    private static final String WORKSTATION_ID_TAG = "WorkstationId";
    private static final ResourceLocation CRAFTING_TABLE_ID = ResourceLocation.withDefaultNamespace("crafting_table");
    private static final ResourceLocation FURNACE_ID = ResourceLocation.withDefaultNamespace("furnace");
    private static final ResourceLocation BLAST_FURNACE_ID = ResourceLocation.withDefaultNamespace("blast_furnace");
    private static final ResourceLocation SMOKER_ID = ResourceLocation.withDefaultNamespace("smoker");
    private static final ResourceLocation CAMPFIRE_ID = ResourceLocation.withDefaultNamespace("campfire");
    private static final ResourceLocation STONECUTTER_ID = ResourceLocation.withDefaultNamespace("stonecutter");
    private static final ResourceLocation SMITHING_TABLE_ID = ResourceLocation.withDefaultNamespace("smithing_table");
    private static final ResourceLocation AE2_INSCRIBER_ID = ResourceLocation.fromNamespaceAndPath("ae2", "inscriber");
    private static final ResourceLocation AE2_CHARGER_ID = ResourceLocation.fromNamespaceAndPath("ae2", "charger");
    private static final ResourceLocation EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("extendedae", "assembler_matrix_speed");
    private static final Map<String, ResourceLocation> RECIPE_TYPE_TO_WORKSTATION = Map.ofEntries(
            Map.entry("minecraft:crafting", CRAFTING_TABLE_ID),
            Map.entry("minecraft:smelting", FURNACE_ID),
            Map.entry("minecraft:blasting", BLAST_FURNACE_ID),
            Map.entry("minecraft:smoking", SMOKER_ID),
            Map.entry("minecraft:campfire_cooking", CAMPFIRE_ID),
            Map.entry("minecraft:stonecutting", STONECUTTER_ID),
            Map.entry("minecraft:smithing", SMITHING_TABLE_ID),
            Map.entry("ae2:inscriber", AE2_INSCRIBER_ID),
            Map.entry("ae2:charger", AE2_CHARGER_ID),
            Map.entry("extendedae:crystal_assembler", EXTENDEDAE_ASSEMBLER_MATRIX_SPEED_ID));

    private PatternEncodingSourceHelper() {
    }

    @Nullable
    public static ResourceLocation resolveWorkstationForTransferRecipe(@Nullable Object recipe) {
        if (recipe instanceof RecipeHolder<?> holder) {
            return resolveWorkstationForRecipe(holder.value());
        }
        if (recipe instanceof Recipe<?> vanillaRecipe) {
            return resolveWorkstationForRecipe(vanillaRecipe);
        }
        return null;
    }

    @Nullable
    public static ResourceLocation resolveWorkstationForRecipe(@Nullable Recipe<?> recipe) {
        if (recipe == null) {
            return null;
        }

        ResourceLocation recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        if (recipeTypeId == null) {
            return null;
        }

        ResourceLocation mappedId = RECIPE_TYPE_TO_WORKSTATION.get(recipeTypeId.toString());
        if (mappedId != null) {
            return mappedId;
        }

        if (BuiltInRegistries.ITEM.containsKey(recipeTypeId)) {
            return recipeTypeId;
        }

        if (BuiltInRegistries.BLOCK.containsKey(recipeTypeId)) {
            return recipeTypeId;
        }

        return null;
    }

    @Nullable
    public static ResourceLocation resolveFallbackWorkstationForMode(@Nullable EncodingMode mode) {
        if (mode == null) {
            return null;
        }

        return switch (mode) {
            case CRAFTING -> CRAFTING_TABLE_ID;
            case STONECUTTING -> STONECUTTER_ID;
            case SMITHING_TABLE -> SMITHING_TABLE_ID;
            case PROCESSING -> null;
        };
    }

    public static void rememberTransferSource(PatternEncodingTermMenu menu, @Nullable Object recipe) {
        if (menu instanceof PatternEncodingSourceAware sourceAware) {
            sourceAware.setPendingPatternSource(resolveWorkstationForTransferRecipe(recipe));
        }
    }

    public static void applyPatternSource(ItemStack stack, PatternEncodingSourceAware sourceAware,
                                          @Nullable ResourceLocation fallbackWorkstationId) {
        ResourceLocation workstationId = sourceAware.getPendingPatternSource();
        if (workstationId == null) {
            workstationId = fallbackWorkstationId;
        }

        if (workstationId != null) {
            writePatternSource(stack, workstationId);
        }
    }

    public static void appendPatternSourceTooltip(ItemStack stack, List<Component> lines) {
        ResourceLocation workstationId = readPatternSource(stack);
        if (workstationId == null) {
            return;
        }

        lines.add(Component.translatable(
                        "tooltip.data_energistics.pattern_encoding_source",
                        resolveWorkstationDisplayName(workstationId))
                .withStyle(ChatFormatting.GRAY));
    }

    public static void writePatternSource(ItemStack stack, ResourceLocation workstationId) {
        if (stack.isEmpty()) {
            return;
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            var rootTag = tag.getCompound(ROOT_TAG);
            rootTag.putString(WORKSTATION_ID_TAG, workstationId.toString());
            tag.put(ROOT_TAG, rootTag);
        });
    }

    @Nullable
    public static ResourceLocation readPatternSource(ItemStack stack) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(ROOT_TAG, net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            return null;
        }

        var rootTag = tag.getCompound(ROOT_TAG);
        String workstationId = rootTag.getString(WORKSTATION_ID_TAG);
        if (workstationId.isEmpty()) {
            return null;
        }

        return ResourceLocation.tryParse(workstationId);
    }

    private static Component resolveWorkstationDisplayName(ResourceLocation workstationId) {
        var item = BuiltInRegistries.ITEM.getOptional(workstationId).orElse(null);
        if (item != null) {
            return item.getDefaultInstance().getHoverName();
        }

        var block = BuiltInRegistries.BLOCK.getOptional(workstationId).orElse(null);
        if (block != null) {
            return block.getName();
        }

        return Component.literal(workstationId.toString());
    }
}
