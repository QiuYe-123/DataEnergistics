package com.fish_dan_.data_energistics.registry;

import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.entity.DispersingDataEntity;
import com.fish_dan_.data_energistics.entity.MatterConvergingBoltEntity;
import com.fish_dan_.data_energistics.entity.TntConfigurablePrimedEntity;
import com.fish_dan_.data_energistics.entity.Tnt0PrimedEntity;
import com.fish_dan_.data_energistics.entity.Tnt1PrimedEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Data_Energistics.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<DispersingDataEntity>> DISPERSING_DATA =
            ENTITY_TYPES.register("dispersing_data", () -> EntityType.Builder
                    .<DispersingDataEntity>of(DispersingDataEntity::new, MobCategory.MISC)
                    .sized(0.125F, 0.125F)
                    .eyeHeight(0.0625F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("dispersing_data"));
    public static final DeferredHolder<EntityType<?>, EntityType<MatterConvergingBoltEntity>> MATTER_CONVERGING_BOLT =
            ENTITY_TYPES.register("matter_converging_bolt", () -> EntityType.Builder
                    .<MatterConvergingBoltEntity>of(MatterConvergingBoltEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("matter_converging_bolt"));
    public static final DeferredHolder<EntityType<?>, EntityType<Tnt0PrimedEntity>> TNT_0_PRIMED =
            ENTITY_TYPES.register("tnt_0_primed", () -> EntityType.Builder
                    .<Tnt0PrimedEntity>of(Tnt0PrimedEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build("tnt_0_primed"));
    public static final DeferredHolder<EntityType<?>, EntityType<Tnt1PrimedEntity>> TNT_1_PRIMED =
            ENTITY_TYPES.register("tnt_1_primed", () -> EntityType.Builder
                    .<Tnt1PrimedEntity>of(Tnt1PrimedEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build("tnt_1_primed"));
    public static final DeferredHolder<EntityType<?>, EntityType<TntConfigurablePrimedEntity>> TNT_CONFIGURABLE_PRIMED =
            ENTITY_TYPES.register("tnt_configurable_primed", () -> EntityType.Builder
                    .<TntConfigurablePrimedEntity>of(TntConfigurablePrimedEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build("tnt_configurable_primed"));

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
