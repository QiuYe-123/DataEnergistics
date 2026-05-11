package com.fish_dan_.data_energistics.item;

import com.fish_dan_.data_energistics.entity.DispersingDataEntity;
import com.fish_dan_.data_energistics.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DataCaptureBallItem extends Item {
    public DataCaptureBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!(entity instanceof DispersingDataEntity dispersingDataEntity)) {
            return false;
        }

        if (!player.level().isClientSide()) {
            ItemStack reward = new ItemStack(ModItems.RESIDUAL_DATA.get());
            if (!player.addItem(reward)) {
                player.drop(reward, false);
            }
            stack.shrink(1);
            dispersingDataEntity.discard();
        }

        return true;
    }
}
