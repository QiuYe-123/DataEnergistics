package com.fish_dan_.data_energistics.menu.common;

import appeng.api.stacks.GenericStack;
import org.jetbrains.annotations.Nullable;

public interface PatternEncodingTransferKeyAware {
    void dataEnergistics$sendTransferKeyInputAction(@Nullable String serializedKeyInput);

    void dataEnergistics$sendTransferFluidInputsAction(@Nullable String serializedFluidInputs);

    void dataEnergistics$sendTransferFluidOutputsAction(@Nullable String serializedFluidOutputs);

    @Nullable
    GenericStack dataEnergistics$getDisplayedTransferKeyInput();

    void dataEnergistics$setDisplayedTransferKeyInputSerialized(@Nullable String serializedKeyInput);

    @Nullable
    String dataEnergistics$getDisplayedTransferKeyInputSerialized();
}
