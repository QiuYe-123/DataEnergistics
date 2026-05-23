package com.fish_dan_.data_energistics.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.localization.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IItemExtension;

import java.util.List;

public interface PoweredEnergyItem extends IAEItemPowerStorage, IItemExtension {
    double MAX_POWER = 20_000.0D;
    double CHARGE_RATE = 20_000.0D;
    double ENERGY_PER_ACTION = 100.0D;

    default void appendEnergyHoverText(ItemStack stack, List<Component> lines) {
        lines.add(Tooltips.energyStorageComponent(this.getAECurrentPower(stack), this.getAEMaxPower(stack)));
    }

    default boolean isEnergyBarVisible(ItemStack stack) {
        return true;
    }

    default int getEnergyBarWidth(ItemStack stack) {
        return Mth.clamp((int) Math.round(this.getAECurrentPower(stack) / this.getAEMaxPower(stack) * 13.0D), 0, 13);
    }

    default int getEnergyBarColor(ItemStack stack) {
        return Mth.hsvToRgb(1.0F / 3.0F, 1.0F, 1.0F);
    }

    default void consumeActionEnergy(ItemStack stack) {
        this.extractAEPower(stack, ENERGY_PER_ACTION, Actionable.MODULATE);
    }

    default boolean hasSufficientEnergy(ItemStack stack) {
        return this.getAECurrentPower(stack) >= ENERGY_PER_ACTION;
    }

    default float getUnpoweredDestroySpeed(ItemStack stack, BlockState state) {
        return 1.0F;
    }

    static boolean isAnvilRepairBlocked(ItemStack baseStack, ItemStack additionStack) {
        return baseStack.getItem() instanceof PoweredEnergyItem
                && !additionStack.isEmpty()
                && baseStack.isDamageableItem()
                && additionStack.is(baseStack.getItem())
                && additionStack.isDamageableItem();
    }

    static boolean canCraftWithEnergy(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof PoweredEnergyItem poweredItem && !poweredItem.hasSufficientEnergy(stack)) {
                return false;
            }
        }
        return true;
    }

    @Override
    default double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        double maxStorage = this.getAEMaxPower(stack);
        double currentStorage = this.getAECurrentPower(stack);
        double required = maxStorage - currentStorage;
        double overflow = Math.max(0.0D, Math.min(amount - required, amount));
        if (mode == Actionable.MODULATE) {
            double toAdd = Math.min(amount, required);
            this.setAECurrentPower(stack, currentStorage + toAdd);
        }
        return overflow;
    }

    @Override
    default double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        double currentStorage = this.getAECurrentPower(stack);
        double fulfillable = Math.min(amount, currentStorage);
        if (mode == Actionable.MODULATE) {
            this.setAECurrentPower(stack, currentStorage - fulfillable);
        }
        return fulfillable;
    }

    @Override
    default double getAEMaxPower(ItemStack stack) {
        return MAX_POWER;
    }

    @Override
    default double getAECurrentPower(ItemStack stack) {
        return stack.getOrDefault(AEComponents.STORED_ENERGY, 0.0D);
    }

    private void setAECurrentPower(ItemStack stack, double power) {
        if (power < 1.0E-4D) {
            stack.remove(AEComponents.STORED_ENERGY);
        } else {
            stack.set(AEComponents.STORED_ENERGY, power);
        }
    }

    @Override
    default AccessRestriction getPowerFlow(ItemStack stack) {
        return AccessRestriction.WRITE;
    }

    @Override
    default double getChargeRate(ItemStack stack) {
        return CHARGE_RATE;
    }

    @Override
    default boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return !this.hasSufficientEnergy(stack);
    }

    @Override
    default boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    default ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        if (!this.hasSufficientEnergy(itemStack)) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = itemStack.copyWithCount(1);
        this.consumeActionEnergy(remainder);
        return remainder;
    }
}
