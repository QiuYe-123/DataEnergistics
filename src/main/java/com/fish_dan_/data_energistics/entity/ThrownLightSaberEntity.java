package com.fish_dan_.data_energistics.entity;

import com.fish_dan_.data_energistics.registry.ModEntities;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownLightSaberEntity extends AbstractArrow implements ItemSupplier {
    private static final int DESPAWN_TICKS = 20 * 60 * 5;
    private static final String TAG_DEALT_DAMAGE = "DealtDamage";
    private static final EntityDataAccessor<ItemStack> DATA_SABER_STACK =
            SynchedEntityData.defineId(ThrownLightSaberEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Byte> ID_LOYALTY =
            SynchedEntityData.defineId(ThrownLightSaberEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL =
            SynchedEntityData.defineId(ThrownLightSaberEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;
    private ItemStack saberStack = ItemStack.EMPTY;

    public ThrownLightSaberEntity(EntityType<? extends ThrownLightSaberEntity> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.ALLOWED;
    }

    public ThrownLightSaberEntity(Level level, LivingEntity shooter, ItemStack saberStack) {
        super(ModEntities.THROWN_LIGHT_SABER.get(), shooter, level, saberStack.copyWithCount(1), saberStack.copyWithCount(1));
        this.setSaberStack(saberStack);
        this.pickup = Pickup.ALLOWED;
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(saberStack));
        this.entityData.set(ID_FOIL, saberStack.hasFoil());
    }

    public ThrownLightSaberEntity(Level level, double x, double y, double z, ItemStack saberStack) {
        super(ModEntities.THROWN_LIGHT_SABER.get(), x, y, z, level, saberStack.copyWithCount(1), saberStack.copyWithCount(1));
        this.setSaberStack(saberStack);
        this.pickup = Pickup.ALLOWED;
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(saberStack));
        this.entityData.set(ID_FOIL, saberStack.hasFoil());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SABER_STACK, ItemStack.EMPTY);
        builder.define(ID_LOYALTY, (byte) 0);
        builder.define(ID_FOIL, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && owner != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.discard();
                return;
            }

            this.setNoPhysics(true);
            Vec3 toOwner = owner.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + toOwner.y * 0.015D * loyalty, this.getZ());
            if (this.level().isClientSide) {
                this.yOld = this.getY();
            }

            double acceleration = 0.05D * loyalty;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(toOwner.normalize().scale(acceleration)));
            if (this.clientSideReturnTridentTickCount == 0) {
                this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.clientSideReturnTridentTickCount;
        }

        super.tick();
    }

    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        float damage = this.getBaseThrownDamage();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.damageSources().trident(this, owner == null ? this : owner);
        if (this.level() instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), target, damageSource, damage);
        }

        this.dealtDamage = true;
        if (target.hurt(damageSource, damage)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, damageSource, this.getWeaponItem());
            }

            if (target instanceof LivingEntity livingTarget) {
                this.doKnockback(livingTarget, damageSource);
                this.doPostHurtEffects(livingTarget);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

     @Override
     protected void hitBlockEnchantmentEffects(ServerLevel serverLevel, BlockHitResult blockHitResult, ItemStack stack) {
         Vec3 impact = blockHitResult.getBlockPos().clampLocationWithin(blockHitResult.getLocation());
         LivingEntity attacker = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
         EnchantmentHelper.onHitBlock(
                serverLevel,
                stack,
                 attacker,
                 this,
                 (EquipmentSlot) null,
                 impact,
                 serverLevel.getBlockState(blockHitResult.getBlockPos()),
                ignored -> {
                }
         );
     }

    @Override
    public ItemStack getItem() {
        ItemStack stack = this.getEntityData().get(DATA_SABER_STACK);
        if (stack.isEmpty()) {
            stack = this.getWeaponItem();
        }
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    @Override
    public ItemStack getWeaponItem() {
        ItemStack stack = this.getPickupItemStackOrigin();
        if (stack == null || stack.isEmpty()) {
            return this.saberStack;
        }
        return stack;
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        ItemStack stack = this.getPickupItemStackOrigin();
        if (stack == null || stack.isEmpty()) {
            stack = this.saberStack;
        }
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stack.copy();
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    public boolean isEmbedded() {
        return this.inGround || this.inGroundTime > 0;
    }

    public int getEmbeddedTime() {
        return this.inGroundTime;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(TAG_DEALT_DAMAGE, this.dealtDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.dealtDamage = tag.getBoolean(TAG_DEALT_DAMAGE);
        ItemStack origin = this.getPickupItemStackOrigin();
        this.setSaberStack(origin == null ? ItemStack.EMPTY : origin);
        this.entityData.set(ID_LOYALTY, this.getLoyaltyFromItem(this.saberStack));
        this.entityData.set(ID_FOIL, !this.saberStack.isEmpty() && this.saberStack.hasFoil());
    }

    @Override
    public void tickDespawn() {
        if (this.inGroundTime >= DESPAWN_TICKS) {
            this.discard();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    private byte getLoyaltyFromItem(ItemStack stack) {
        if (this.level() instanceof ServerLevel serverLevel) {
            return (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, stack, this), 0, 127);
        }
        return 0;
    }

    private void setSaberStack(ItemStack stack) {
        this.saberStack = stack == null ? ItemStack.EMPTY : stack.copyWithCount(1);
        this.getEntityData().set(DATA_SABER_STACK, this.saberStack.copy());
        this.setPickupItemStack(this.saberStack.copy());
    }

    private float getBaseThrownDamage() {
        ItemStack weapon = this.getWeaponItem();
        if (weapon.isEmpty()) {
            return 1.0F;
        }

        final double playerBaseDamage = 1.0D;
        final double[] addValue = {0.0D};
        final double[] addMultipliedBase = {0.0D};
        final double[] addMultipliedTotal = {0.0D};

        weapon.forEachModifier(EquipmentSlot.MAINHAND, (Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, AttributeModifier modifier) -> {
            if (!attribute.equals(Attributes.ATTACK_DAMAGE)) {
                return;
            }

            if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                addValue[0] += modifier.amount();
            } else if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                addMultipliedBase[0] += modifier.amount();
            } else if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                addMultipliedTotal[0] += modifier.amount();
            }
        });

        double damage = playerBaseDamage + addValue[0];
        damage += playerBaseDamage * addMultipliedBase[0];
        damage *= 1.0D + addMultipliedTotal[0];
        return Math.max(0.0F, (float) damage + 1.0F);
    }

    private boolean isAcceptibleReturnOwner() {
        Entity owner = this.getOwner();
        return owner != null && owner.isAlive() && (!(owner instanceof ServerPlayer serverPlayer) || !serverPlayer.isSpectator());
    }
}
