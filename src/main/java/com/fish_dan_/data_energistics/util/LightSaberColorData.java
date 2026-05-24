package com.fish_dan_.data_energistics.util;

import appeng.api.util.AEColor;
import appeng.items.tools.powered.ColorApplicatorItem;
import com.fish_dan_.data_energistics.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public final class LightSaberColorData {
    private static final String TAG_LIGHT_SABER_COLOR = "light_saber_color";

    private LightSaberColorData() {
    }

    public static boolean isColorableLightSaber(ItemStack stack) {
        return stack.is(ModItems.DATA_LIGHT_SABER.get());
    }

    public static float getModelValue(ItemStack stack) {
        DyeColor color = getStoredColor(stack);
        return color == null ? 0.0F : (color.getId() + 1) / 16.0F;
    }

    public static @Nullable DyeColor getStoredColor(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(TAG_LIGHT_SABER_COLOR)) {
            return null;
        }

        if (tag.contains(TAG_LIGHT_SABER_COLOR, CompoundTag.TAG_STRING)) {
            String name = tag.getString(TAG_LIGHT_SABER_COLOR);
            for (DyeColor value : DyeColor.values()) {
                if (value.getName().equals(name)) {
                    return value;
                }
            }
        }

        if (tag.contains(TAG_LIGHT_SABER_COLOR, CompoundTag.TAG_INT)) {
            int legacyColor = tag.getInt(TAG_LIGHT_SABER_COLOR) & 0xFFFFFF;
            DyeColor nearest = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (DyeColor value : DyeColor.values()) {
                int diffuse = value.getTextureDiffuseColor() & 0xFFFFFF;
                int distance = colorDistanceSquared(legacyColor, diffuse);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = value;
                }
            }
            return nearest;
        }

        return null;
    }

    public static ItemStack withColor(ItemStack stack, DyeColor color) {
        ItemStack result = stack.copy();
        CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.putString(TAG_LIGHT_SABER_COLOR, color.getName()));
        return result;
    }

    public static @Nullable DyeColor getColorFromIngredient(ItemStack stack) {
        if (stack.getItem() instanceof DyeItem dyeItem) {
            return dyeItem.getDyeColor();
        }
        if (stack.getItem() instanceof ColorApplicatorItem colorApplicatorItem) {
            AEColor aeColor = colorApplicatorItem.getActiveColor(stack);
            if (aeColor != null && aeColor != AEColor.TRANSPARENT && aeColor.dye != null) {
                return aeColor.dye;
            }
        }
        return null;
    }

    private static int colorDistanceSquared(int first, int second) {
        int redDelta = ((first >> 16) & 0xFF) - ((second >> 16) & 0xFF);
        int greenDelta = ((first >> 8) & 0xFF) - ((second >> 8) & 0xFF);
        int blueDelta = (first & 0xFF) - (second & 0xFF);
        return redDelta * redDelta + greenDelta * greenDelta + blueDelta * blueDelta;
    }
}
