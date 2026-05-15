package com.fish_dan_.data_energistics.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DispersingDataEntity extends Entity {
    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(DispersingDataEntity.class, EntityDataSerializers.INT);
    private static final int LIFETIME_TICKS = 1200;
    private static final double DRIFT_STRENGTH = 0.015D;
    private static final double VERTICAL_DRIFT = 0.01D;
    private int age;

    public DispersingDataEntity(EntityType<? extends DispersingDataEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TEXTURE_VARIANT, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        Vec3 drift = new Vec3(
                (this.random.nextDouble() - 0.5D) * DRIFT_STRENGTH,
                (this.random.nextDouble() - 0.5D) * VERTICAL_DRIFT,
                (this.random.nextDouble() - 0.5D) * DRIFT_STRENGTH
        );
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92D).add(drift));
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.level() instanceof ServerLevel && ++this.age >= LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.age = tag.getInt("Age");
        if (tag.contains("TextureVariant")) {
            this.setTextureVariant(tag.getInt("TextureVariant"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", this.age);
        tag.putInt("TextureVariant", this.getTextureVariant());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(TEXTURE_VARIANT, Math.floorMod(variant, 4));
    }
}
