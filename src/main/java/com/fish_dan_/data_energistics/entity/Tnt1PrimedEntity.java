package com.fish_dan_.data_energistics.entity;

import com.fish_dan_.data_energistics.FlatteningTntConfig;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class Tnt1PrimedEntity extends AbstractFlatteningTntPrimedEntity {
    public Tnt1PrimedEntity(EntityType<? extends Tnt1PrimedEntity> entityType, Level level) {
        super(entityType, level);
    }

    public Tnt1PrimedEntity(Level level, BlockPos origin, @Nullable LivingEntity owner) {
        super(ModEntities.TNT_1_PRIMED.get(), level, origin, owner, ModBlocks.TNT_1.get().defaultBlockState());
    }

    @Override
    protected FlatteningTntConfig.Definition getDefinition() {
        return FlatteningTntConfig.tnt1;
    }
}
