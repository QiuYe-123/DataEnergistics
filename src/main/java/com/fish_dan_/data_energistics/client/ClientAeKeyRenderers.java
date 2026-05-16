package com.fish_dan_.data_energistics.client;

import appeng.api.client.AEKeyRendering;
import com.fish_dan_.data_energistics.ae2.DataFlowKey;
import com.fish_dan_.data_energistics.ae2.DataFlowKeyType;

public final class ClientAeKeyRenderers {
    private static boolean registered;

    private ClientAeKeyRenderers() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        AEKeyRendering.register(DataFlowKeyType.TYPE, DataFlowKey.class, new DataFlowKeyRenderHandler());
    }
}
