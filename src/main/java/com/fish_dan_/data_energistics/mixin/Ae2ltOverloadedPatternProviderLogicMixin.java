package com.fish_dan_.data_energistics.mixin;

import com.fish_dan_.data_energistics.accessor.PatternProviderHostAccessor;
import com.fish_dan_.data_energistics.accessor.PatternProviderLogicAccessor;
import com.fish_dan_.data_energistics.ae2.RedstoneTuningAutoRequestHelper;
import com.fish_dan_.data_energistics.ae2.RedstoneTuningMode;
import com.moakiee.ae2lt.logic.OverloadedPatternProviderLogic;
import com.moakiee.ae2lt.blockentity.OverloadedPatternProviderBlockEntity;
import appeng.api.stacks.GenericStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(OverloadedPatternProviderLogic.class)
public abstract class Ae2ltOverloadedPatternProviderLogicMixin implements PatternProviderLogicAccessor {
    @Shadow
    @Final
    private OverloadedPatternProviderBlockEntity overloadedHost;

    @Unique
    private boolean dataEnergistics$dispatchPulsePending;
    @Shadow(remap = false)
    private List<GenericStack> wirelessSendList;

    @Inject(method = "pushPattern", at = @At("RETURN"))
    private void dataEnergistics$afterPushPattern(appeng.api.crafting.IPatternDetails patternDetails,
                                                  appeng.api.stacks.KeyCounter[] inputHolder,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }
        this.dataEnergistics$dispatchPulsePending = true;
        this.dataEnergistics$tryFinishDispatchPulse();
    }

    @Inject(method = "onNeighborChanged", at = @At("HEAD"))
    private void dataEnergistics$handlePulseUnlock(CallbackInfo ci) {
        if (!(this.overloadedHost instanceof PatternProviderHostAccessor accessor)) {
            return;
        }
        accessor.dataEnergistics$scheduleRedstoneInputCheck();
    }

    @Inject(method = "tickAutoReturn", at = @At("HEAD"))
    private void dataEnergistics$tickRedstoneEmitter(CallbackInfo ci) {
        if (this.overloadedHost instanceof PatternProviderHostAccessor accessor) {
            accessor.dataEnergistics$serverTick();
            this.dataEnergistics$tryConsumePulseUnlock(accessor);
        }
        this.dataEnergistics$tryFinishDispatchPulse();
    }

    @Override
    public boolean dataEnergistics$forcePulseUnlock() {
        if (this.overloadedHost instanceof PatternProviderHostAccessor accessor
                && accessor.dataEnergistics$getRedstoneTuningMode() == RedstoneTuningMode.PULSE_TO_UNLOCK_ONCE
                && this.overloadedHost.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            RedstoneTuningAutoRequestHelper.requestPrimaryOutputs(
                    serverLevel,
                    this.overloadedHost.getGrid(),
                    ((PatternProviderLogicFieldAccessor) this).dataEnergistics$getActionSource(),
                    ((OverloadedPatternProviderLogic) (Object) this).getAvailablePatterns()
            );
            return true;
        }
        return false;
    }

    @Unique
    private void dataEnergistics$tryFinishDispatchPulse() {
        if (!this.dataEnergistics$dispatchPulsePending) {
            return;
        }
        if (!((PatternProviderLogicFieldAccessor) this).dataEnergistics$getSendList().isEmpty()
                || !this.wirelessSendList.isEmpty()) {
            return;
        }
        this.dataEnergistics$dispatchPulsePending = false;
        if (this.overloadedHost instanceof PatternProviderHostAccessor accessor) {
            accessor.dataEnergistics$onRedstoneTuningDispatch();
        }
    }

    @Unique
    private void dataEnergistics$tryConsumePulseUnlock(PatternProviderHostAccessor accessor) {
        if (!accessor.dataEnergistics$hasRedstoneTuningCard()
                || accessor.dataEnergistics$getRedstoneTuningMode() != RedstoneTuningMode.PULSE_TO_UNLOCK_ONCE
                || !accessor.dataEnergistics$consumeRedstoneInputPulse()
                || !(this.overloadedHost.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        RedstoneTuningAutoRequestHelper.requestPrimaryOutputs(
                serverLevel,
                this.overloadedHost.getGrid(),
                ((PatternProviderLogicFieldAccessor) this).dataEnergistics$getActionSource(),
                ((OverloadedPatternProviderLogic) (Object) this).getAvailablePatterns()
        );
    }
}
