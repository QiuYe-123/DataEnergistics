package com.fish_dan_.data_energistics.client.screen;

import java.util.ArrayList;
import java.util.List;

import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.Point;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.util.ReadableNumberConverter;
import com.fish_dan_.data_energistics.Data_Energistics;
import com.fish_dan_.data_energistics.menu.universal.UniversalPatternEncodingTermMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class UniversalPatternEncodingTermScreen extends PatternEncodingTermScreen<UniversalPatternEncodingTermMenu> {
    private static final ResourceLocation PREVIEW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Data_Energistics.MODID, "textures/gui/pattern_writer_preview.png");
    private static final int PREVIEW_PANEL_WIDTH = 192;
    private static final int PREVIEW_PANEL_HEIGHT = 176;
    private static final int PREVIEW_PANEL_MARGIN = 0;
    private static final int PREVIEW_PANEL_Y_OFFSET = 110;
    private boolean previewVisible;

    public UniversalPatternEncodingTermScreen(UniversalPatternEncodingTermMenu menu, Inventory playerInventory,
                                              Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && isOverEncodeButton(mouseX, mouseY)) {
            this.menu.encode();
            this.previewVisible = true;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (!this.previewVisible) {
            return;
        }

        Rect2i previewBounds = getPreviewPanelBounds();
        guiGraphics.blit(PREVIEW_TEXTURE,
                previewBounds.getX(), previewBounds.getY(),
                0, 0.0F, 0.0F,
                previewBounds.getWidth(), previewBounds.getHeight(),
                PREVIEW_PANEL_WIDTH, PREVIEW_PANEL_HEIGHT);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        if (this.menu.getSlotSemantic(slot) != SlotSemantics.BLANK_PATTERN) {
            return;
        }

        long networkCount = this.menu.networkBlankPatternCount;
        int localCount = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
        long totalCount = localCount + networkCount;
        if (totalCount <= 0) {
            return;
        }

        if (networkCount > 0) {
            if (slot.getItem().isEmpty()) {
                guiGraphics.renderItem(AEItems.BLANK_PATTERN.stack(), slot.x, slot.y);
            }
            StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, slot.x, slot.y,
                    ReadableNumberConverter.format(totalCount, 4));
        }
    }

    @Override
    public List<Rect2i> getExclusionZones() {
        List<Rect2i> zones = new ArrayList<>(super.getExclusionZones());
        if (this.previewVisible) {
            zones.add(getPreviewPanelBounds());
        }
        return zones;
    }

    private boolean isOverEncodeButton(double mouseX, double mouseY) {
        WidgetStyle buttonStyle = this.getStyle().getWidget("encodePattern");
        Point position = buttonStyle.resolve(new Rect2i(this.leftPos, this.topPos, this.imageWidth, this.imageHeight));
        int width = buttonStyle.getWidth() > 0 ? buttonStyle.getWidth() : 16;
        int height = buttonStyle.getHeight() > 0 ? buttonStyle.getHeight() : 16;
        return mouseX >= position.getX() && mouseX < position.getX() + width
                && mouseY >= position.getY() && mouseY < position.getY() + height;
    }

    private Rect2i getPreviewPanelBounds() {
        int preferredX = this.leftPos + this.imageWidth + PREVIEW_PANEL_MARGIN;
        if (preferredX + PREVIEW_PANEL_WIDTH > this.width - 4) {
            preferredX = this.leftPos - PREVIEW_PANEL_WIDTH - PREVIEW_PANEL_MARGIN;
        }

        int x = Math.max(4, Math.min(preferredX, this.width - PREVIEW_PANEL_WIDTH - 4));
        int preferredY = this.topPos + PREVIEW_PANEL_Y_OFFSET;
        int y = Math.max(4, Math.min(preferredY, this.height - PREVIEW_PANEL_HEIGHT - 4));
        return new Rect2i(x, y, PREVIEW_PANEL_WIDTH, PREVIEW_PANEL_HEIGHT);
    }
}
