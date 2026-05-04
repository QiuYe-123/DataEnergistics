package com.fish_dan_.data_energistics.client.screen;

import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.items.PatternEncodingTermMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class NativePatternEncodingTermScreen extends PatternEncodingPreviewScreen<PatternEncodingTermMenu> {
    private static final int AE2_PREVIEW_PANEL_Y_OFFSET = 105;
    private static final int AE2_PREVIEW_SCROLLBAR_SCREEN_X = 309;
    private static final int AE2_PREVIEW_SCROLLBAR_SCREEN_Y = 121;
    private static final int AE2_PREVIEW_SCROLLBAR_HEIGHT = 104;
    private static final int AE2_SEARCH_BOX_X = 42;
    private static final int AE2_SEARCH_BOX_Y = 6;

    public NativePatternEncodingTermScreen(PatternEncodingTermMenu menu, Inventory playerInventory,
                                           Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected int getPreviewPanelYOffset() {
        return AE2_PREVIEW_PANEL_Y_OFFSET;
    }

    @Override
    protected int getPreviewScrollbarScreenX() {
        return AE2_PREVIEW_SCROLLBAR_SCREEN_X;
    }

    @Override
    protected int getPreviewScrollbarScreenY() {
        return AE2_PREVIEW_SCROLLBAR_SCREEN_Y;
    }

    @Override
    protected int getPreviewScrollbarHeight() {
        return AE2_PREVIEW_SCROLLBAR_HEIGHT;
    }

    @Override
    protected int getSearchBoxX() {
        return AE2_SEARCH_BOX_X;
    }

    @Override
    protected int getSearchBoxY() {
        return AE2_SEARCH_BOX_Y;
    }
}
