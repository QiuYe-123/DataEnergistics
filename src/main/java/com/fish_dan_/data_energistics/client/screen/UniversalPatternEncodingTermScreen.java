package com.fish_dan_.data_energistics.client.screen;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import com.fish_dan_.data_energistics.menu.universal.UniversalPatternEncodingTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class UniversalPatternEncodingTermScreen extends PatternEncodingTermScreen<UniversalPatternEncodingTermMenu> {
    public UniversalPatternEncodingTermScreen(UniversalPatternEncodingTermMenu menu, Inventory playerInventory,
                                              Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
