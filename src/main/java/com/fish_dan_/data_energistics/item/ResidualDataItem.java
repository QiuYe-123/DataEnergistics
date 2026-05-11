package com.fish_dan_.data_energistics.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ResidualDataItem extends Item {
    private static final int TRIGGER_INTERVAL_TICKS = 1200;
    private static final int EFFECT_DURATION_TICKS = 100;
    private static final double BASE_TRIGGER_CHANCE = 0.05D;
    private static final double PER_ITEM_TRIGGER_CHANCE = 0.001D;
    private static final Holder<MobEffect>[] RANDOM_EFFECTS = new Holder[] {
            MobEffects.REGENERATION,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DAMAGE_BOOST,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.DIG_SPEED,
            MobEffects.ABSORPTION,
            MobEffects.LUCK,
            MobEffects.JUMP,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SLOWDOWN,
            MobEffects.BLINDNESS,
            MobEffects.HUNGER,
            MobEffects.WITHER,
            MobEffects.CONFUSION
    };

    public ResidualDataItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (level.isClientSide() || !(entity instanceof Player player) || player.tickCount % TRIGGER_INTERVAL_TICKS != 0) {
            return;
        }

        int firstSlot = findFirstResidualDataSlot(player);
        if (slotId != firstSlot) {
            return;
        }

        int itemCount = countResidualData(player);
        if (itemCount <= 0) {
            return;
        }

        double triggerChance = Math.min(1.0D, BASE_TRIGGER_CHANCE + itemCount * PER_ITEM_TRIGGER_CHANCE);
        if (level.random.nextDouble() >= triggerChance) {
            return;
        }

        Holder<MobEffect> effect = RANDOM_EFFECTS[level.random.nextInt(RANDOM_EFFECTS.length)];
        player.addEffect(new MobEffectInstance(effect, EFFECT_DURATION_TICKS, 0));
    }

    private static int countResidualData(Player player) {
        int total = 0;
        Inventory inventory = player.getInventory();

        for (ItemStack stack : inventory.items) {
            if (stack.getItem() instanceof ResidualDataItem) {
                total += stack.getCount();
            }
        }

        for (ItemStack stack : inventory.offhand) {
            if (stack.getItem() instanceof ResidualDataItem) {
                total += stack.getCount();
            }
        }

        return total;
    }

    private static int findFirstResidualDataSlot(Player player) {
        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.items.size(); i++) {
            if (inventory.items.get(i).getItem() instanceof ResidualDataItem) {
                return i;
            }
        }

        for (int i = 0; i < inventory.offhand.size(); i++) {
            if (inventory.offhand.get(i).getItem() instanceof ResidualDataItem) {
                return 40 + i;
            }
        }

        return -1;
    }
}
