package com.fish_dan_.data_energistics.mixin;

import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import com.fish_dan_.data_energistics.accessor.CondenserBlockEntityAccessor;
import com.fish_dan_.data_energistics.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CondenserBlockEntity.class)
public abstract class CondenserBlockEntityMixin implements CondenserBlockEntityAccessor {
    @Unique
    private static final String DATA_ENERGISTICS_DATA_CAPTURE_BALL_MODE = "dataEnergisticsDataCaptureBallMode";
    @Unique
    private static final double DATA_ENERGISTICS_DATA_CAPTURE_BALL_REQUIRED_POWER = 256 * 1024 * 8;

    @Shadow
    @Final
    private AppEngInternalInventory storageSlot;

    @Unique
    private boolean dataEnergistics$dataCaptureBallMode;

    @Inject(method = "getOutput", at = @At("HEAD"), cancellable = true)
    private void dataEnergistics$replaceDataCaptureBallOutput(CallbackInfoReturnable<ItemStack> cir) {
        if (!this.dataEnergistics$dataCaptureBallMode) {
            return;
        }

        if (!this.storageSlot.getStackInSlot(0).is(ModItems.DATA_STORAGE_COMPONENT_256K.get())) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        cir.setReturnValue(new ItemStack(ModItems.DATA_CAPTURE_BALL.get()));
    }

    @Inject(method = "getRequiredPower", at = @At("HEAD"), cancellable = true)
    private void dataEnergistics$replaceRequiredPower(CallbackInfoReturnable<Double> cir) {
        if (this.dataEnergistics$dataCaptureBallMode) {
            cir.setReturnValue(DATA_ENERGISTICS_DATA_CAPTURE_BALL_REQUIRED_POWER);
        }
    }

    @Inject(method = "getStorage", at = @At("HEAD"), cancellable = true)
    private void dataEnergistics$restrictStorageComponent(CallbackInfoReturnable<Double> cir) {
        if (!this.dataEnergistics$dataCaptureBallMode) {
            return;
        }

        if (this.storageSlot.getStackInSlot(0).is(ModItems.DATA_STORAGE_COMPONENT_256K.get())) {
            cir.setReturnValue(DATA_ENERGISTICS_DATA_CAPTURE_BALL_REQUIRED_POWER);
        } else {
            cir.setReturnValue(0.0D);
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void dataEnergistics$saveDataCaptureBallMode(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries,
                                                         CallbackInfo ci) {
        tag.putBoolean(DATA_ENERGISTICS_DATA_CAPTURE_BALL_MODE, this.dataEnergistics$dataCaptureBallMode);
    }

    @Inject(method = "loadTag", at = @At("TAIL"))
    private void dataEnergistics$loadDataCaptureBallMode(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries,
                                                         CallbackInfo ci) {
        this.dataEnergistics$dataCaptureBallMode = tag.getBoolean(DATA_ENERGISTICS_DATA_CAPTURE_BALL_MODE);
    }

    @Override
    public boolean dataEnergistics$isDataCaptureBallMode() {
        return this.dataEnergistics$dataCaptureBallMode;
    }

    @Override
    public void dataEnergistics$setDataCaptureBallMode(boolean enabled) {
        if (this.dataEnergistics$dataCaptureBallMode == enabled) {
            return;
        }

        this.dataEnergistics$dataCaptureBallMode = enabled;
        var condenser = (CondenserBlockEntity) (Object) this;
        condenser.setChanged();
        condenser.addPower(0);
    }
}
