package com.fish_dan_.data_energistics.ae2;

import appeng.api.stacks.AEKeyType;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class ModAE2Keys {
    private ModAE2Keys() {
    }

    public static void register(RegisterEvent event) {
        event.register(AEKeyType.REGISTRY_KEY, DataFlowKeyType.TYPE.getId(), () -> DataFlowKeyType.TYPE);
    }
}
