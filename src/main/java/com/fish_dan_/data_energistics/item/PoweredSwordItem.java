package com.fish_dan_.data_energistics.item;

import appeng.api.util.AEColor;
import appeng.items.tools.powered.ColorApplicatorItem;
import com.fish_dan_.data_energistics.entity.ThrownLightSaberEntity;
import com.fish_dan_.data_energistics.util.LightSaberColorData;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.slf4j.Logger;

import java.util.List;

public class PoweredSwordItem extends SwordItem implements PoweredEnergyItem, ProjectileItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int THROW_THRESHOLD_TIME = 10;
    private static final float THROW_POWER = 2.5F;
    private final boolean throwable;

    public PoweredSwordItem(Tier tier, Properties properties) {
        this(tier, properties, true);
    }

    public PoweredSwordItem(Tier tier, Properties properties, boolean throwable) {
        super(tier, properties);
        this.throwable = throwable;
    }

    public PoweredSwordItem(Tier tier, Properties properties, Tool toolComponent) {
        this(tier, properties, toolComponent, true);
    }

    public PoweredSwordItem(Tier tier, Properties properties, Tool toolComponent, boolean throwable) {
        super(tier, properties, toolComponent);
        this.throwable = throwable;
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
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility)
                || this.throwable && ItemAbilities.DEFAULT_TRIDENT_ACTIONS.contains(itemAbility);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return this.throwable ? UseAnim.SPEAR : UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.throwable ? 72000 : 0;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return this.throwable;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!this.hasSufficientEnergy(stack)) {
            return false;
        }
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide) {
            this.consumeActionEnergy(stack);
        }
        return result;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack carriedStack, Slot slot,
            ClickAction clickAction, Player player, SlotAccess carriedSlotAccess) {
        if (clickAction != ClickAction.SECONDARY || !LightSaberColorData.isColorableLightSaber(stack)) {
            return false;
        }

        DyeColor color = LightSaberColorData.getColorFromIngredient(carriedStack);
        if (color == null || color == LightSaberColorData.getStoredColor(stack)) {
            return false;
        }

        slot.set(LightSaberColorData.withColor(stack, color));

        if (player.getAbilities().instabuild) {
            return true;
        }

        if (carriedStack.getItem() instanceof ColorApplicatorItem colorApplicatorItem) {
            AEColor aeColor = colorApplicatorItem.getActiveColor(carriedStack);
            if (aeColor != null && aeColor != AEColor.TRANSPARENT && aeColor.dye != null) {
                ItemStack updatedApplicator = carriedStack.copy();
                colorApplicatorItem.consumeColor(updatedApplicator, aeColor, false);
                carriedSlotAccess.set(updatedApplicator);
            }
            return true;
        }

        if (carriedStack.getItem() instanceof DyeItem) {
            ItemStack updatedDye = carriedStack.copy();
            updatedDye.shrink(1);
            carriedSlotAccess.set(updatedDye);
            return true;
        }

        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.throwable) {
            return InteractionResultHolder.pass(stack);
        }
        if (!this.hasSufficientEnergy(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        if (EnchantmentHelper.getTridentSpinAttackStrength(stack, player) > 0.0F && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!this.throwable || !(livingEntity instanceof Player player)) {
            return;
        }

        int useTicks = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (useTicks < this.getThrowThresholdTime(stack)) {
            return;
        }

        float spinStrength = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
        if ((spinStrength > 0.0F && !player.isInWaterOrRain()) || !this.hasSufficientEnergy(stack)) {
            return;
        }

        Holder<SoundEvent> sound = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                .orElse(SoundEvents.TRIDENT_THROW);
        if (!level.isClientSide) {
            LOGGER.info("Light saber releaseUsing: item={}, useTicks={}, spinStrength={}, energy={}",
                    stack.getItem(), useTicks, spinStrength, this.getAECurrentPower(stack));
            if (spinStrength == 0.0F) {
                ItemStack thrownStack = stack.copyWithCount(1);
                this.consumeActionEnergy(stack);
                this.consumeActionEnergy(thrownStack);

                ThrownLightSaberEntity thrownLightSaber = new ThrownLightSaberEntity(level, player, thrownStack);
                thrownLightSaber.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_POWER, 1.0F);
                if (player.hasInfiniteMaterials()) {
                    thrownLightSaber.pickup = Pickup.CREATIVE_ONLY;
                }

                boolean added = level.addFreshEntity(thrownLightSaber);
                LOGGER.info("Light saber projectile spawn: entityType={}, added={}, uuid={}",
                        thrownLightSaber.getType(), added, thrownLightSaber.getUUID());
                level.playSound(null, thrownLightSaber, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.hasInfiniteMaterials()) {
                    player.getInventory().removeItem(stack);
                }
            } else {
                this.consumeActionEnergy(stack);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (spinStrength > 0.0F) {
            float yaw = player.getYRot();
            float pitch = player.getXRot();
            float x = -Mth.sin(yaw * ((float) Math.PI / 180.0F)) * Mth.cos(pitch * ((float) Math.PI / 180.0F));
            float y = -Mth.sin(pitch * ((float) Math.PI / 180.0F));
            float z = Mth.cos(yaw * ((float) Math.PI / 180.0F)) * Mth.cos(pitch * ((float) Math.PI / 180.0F));
            float scale = Mth.sqrt(x * x + y * y + z * z);
            x *= spinStrength / scale;
            y *= spinStrength / scale;
            z *= spinStrength / scale;
            player.push(x, y, z);
            player.startAutoSpinAttack(20, 8.0F, stack);
            if (player.onGround()) {
                player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999F, 0.0D));
            }

            level.playSound(null, player, sound.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    protected int getThrowThresholdTime(ItemStack stack) {
        return Math.max(4, THROW_THRESHOLD_TIME - this.getSpeedCardCount(stack) * 2);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        if (!this.throwable) {
            throw new IllegalStateException("Non-throwable powered sword cannot be converted to a projectile");
        }
        ThrownLightSaberEntity thrownLightSaber = new ThrownLightSaberEntity(
                level,
                pos.x(),
                pos.y(),
                pos.z(),
                stack.copyWithCount(1)
        );
        thrownLightSaber.pickup = Pickup.ALLOWED;
        return thrownLightSaber;
    }
}
