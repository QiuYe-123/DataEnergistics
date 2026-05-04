package com.fish_dan_.data_energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NativePatternEncodingTermScreen extends PatternEncodingPreviewScreen<PatternEncodingTermMenu> {
    public NativePatternEncodingTermScreen(PatternEncodingTermMenu menu, Inventory playerInventory,
                                           Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
