package com.fish_dan_.data_energistics.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class PoweredItem extends Item implements PoweredEnergyItem {
    public PoweredItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, lines, tooltipFlag);
        this.appendEnergyHoverText(stack, lines);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isEnergyBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return this.getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return this.getEnergyBarColor(stack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!this.hasSufficientEnergy(context.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        InteractionResult result = super.useOn(context);
        if (result.consumesAction() && !context.getLevel().isClientSide) {
            this.consumeActionEnergy(context.getItemInHand());
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.hasSufficientEnergy(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        if (result.getResult().consumesAction() && !level.isClientSide) {
            this.consumeActionEnergy(result.getObject());
        }
        return result;
    }
}
