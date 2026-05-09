package com.fish_dan_.data_energistics.client.screen;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.Scrollbar;
import com.fish_dan_.data_energistics.client.widget.DataExtractorToggleButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import com.fish_dan_.data_energistics.menu.DataTeleportAnchorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class DataTeleportAnchorScreen extends AEBaseScreen<DataTeleportAnchorMenu> {
    private static final int ACTION_BUTTON_GAP = 1;
    private static final int VISIBLE_BUTTON_COUNT = 7;
    private static final String ANCHOR_CARDS_WIDGET = "anchor_cards";
    private static final String ANCHOR_CARD_NAME_WIDGET = "anchor_card_name";
    private static final String ANCHOR_CARD_COORDS_WIDGET = "anchor_card_coords";
    private static final String ANCHOR_CARD_DIMENSION_WIDGET = "anchor_card_dimension";
    private static final String SCROLLBAR_WIDGET = "scrollbar";
    private static final ResourceLocation AE2_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/gui/sprites/button.png");
    private static final ResourceLocation AE2_BUTTON_HIGHLIGHTED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/gui/sprites/button_highlighted.png");
    private static final int BUTTON_TEXTURE_WIDTH = 200;
    private static final int BUTTON_TEXTURE_HEIGHT = 20;
    private static final int BUTTON_SLICE_BORDER = 4;
    private static final float CARD_NAME_SCALE = 0.70F;
    private static final float CARD_META_SCALE = 0.60F;

    private final DataExtractorToggleButton redstoneControlButton;
    private final Scrollbar scrollbar;
    private final List<AnchorEntry> anchorEntries = new ArrayList<>();
    private final WidgetStyle anchorCardsStyle;
    private final WidgetStyle anchorCardNameStyle;
    private final WidgetStyle anchorCardCoordsStyle;
    private final WidgetStyle anchorCardDimensionStyle;
    private final WidgetStyle scrollbarStyle;

    public DataTeleportAnchorScreen(DataTeleportAnchorMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.redstoneControlButton = new DataExtractorToggleButton(
                Icon.REDSTONE_ON,
                Icon.REDSTONE_OFF,
                "button.data_energistics.data_teleport_anchor.redstone_control",
                "button.data_energistics.data_teleport_anchor.redstone_control.enabled",
                "button.data_energistics.data_teleport_anchor.redstone_control.disabled",
                this.menu::sendSetRedstoneControlled
        );
        this.addToLeftToolbar(this.redstoneControlButton);
        this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);
        this.anchorCardsStyle = this.getStyle().getWidget(ANCHOR_CARDS_WIDGET);
        this.anchorCardNameStyle = this.getStyle().getWidget(ANCHOR_CARD_NAME_WIDGET);
        this.anchorCardCoordsStyle = this.getStyle().getWidget(ANCHOR_CARD_COORDS_WIDGET);
        this.anchorCardDimensionStyle = this.getStyle().getWidget(ANCHOR_CARD_DIMENSION_WIDGET);
        this.scrollbarStyle = this.getStyle().getWidget(SCROLLBAR_WIDGET);
    }

    @Override
    protected void init() {
        super.init();
        updateFixedScrollbarLayout();
        refreshAnchorEntries();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        setTextContent("dialog_title", Component.translatable(
                this.menu.online
                        ? "screen.data_energistics.data_teleport_anchor.title.online"
                        : "screen.data_energistics.data_teleport_anchor.title.offline"
        ));
        setTextContent("anchor_position", Component.translatable(
                "screen.data_energistics.data_teleport_anchor.anchor",
                formatCoordinates(this.menu.anchorX, this.menu.anchorY, this.menu.anchorZ)
        ));
        setTextContent("anchor_dimension", Component.translatable(
                "screen.data_energistics.data_teleport_anchor.dimension",
                this.menu.anchorDimension
        ));
        this.redstoneControlButton.setState(this.menu.redstoneControlled);
        updateFixedScrollbarLayout();
        refreshAnchorEntries();
    }

    private Component formatCoordinates(int x, int y, int z) {
        return Component.literal("[" + x + ", " + y + ", " + z + "]");
    }

    private int getButtonContentIndex(int visibleIndex) {
        return (this.scrollbar == null ? 0 : this.scrollbar.getCurrentScroll()) + visibleIndex;
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        for (int i = 0; i < VISIBLE_BUTTON_COUNT; i++) {
            int contentIndex = getButtonContentIndex(i);
            if (contentIndex < 0 || contentIndex >= this.anchorEntries.size()) {
                continue;
            }

            Rect2i bounds = getCardBounds(i);
            boolean hovered = isMouseOverCard(mouseX, mouseY, bounds);
            drawNineSlicedTexture(
                    guiGraphics,
                    hovered ? AE2_BUTTON_HIGHLIGHTED_TEXTURE : AE2_BUTTON_TEXTURE,
                    bounds,
                    BUTTON_TEXTURE_WIDTH,
                    BUTTON_TEXTURE_HEIGHT,
                    BUTTON_SLICE_BORDER,
                    BUTTON_SLICE_BORDER,
                    BUTTON_SLICE_BORDER,
                    BUTTON_SLICE_BORDER
            );
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        for (int i = 0; i < VISIBLE_BUTTON_COUNT; i++) {
            int contentIndex = getButtonContentIndex(i);
            if (contentIndex < 0 || contentIndex >= this.anchorEntries.size()) {
                continue;
            }

            Rect2i bounds = getCardBounds(i);
            drawCardText(guiGraphics, bounds, this.anchorEntries.get(contentIndex));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < VISIBLE_BUTTON_COUNT; i++) {
                int contentIndex = getButtonContentIndex(i);
                if (contentIndex < 0 || contentIndex >= this.anchorEntries.size()) {
                    continue;
                }

                Rect2i bounds = getCardBounds(i);
                if (isMouseOverCard((int) mouseX, (int) mouseY, bounds)) {
                    AnchorEntry entry = this.anchorEntries.get(contentIndex);
                    this.menu.sendTeleportToAnchor(entry.dimensionId(), entry.x(), entry.y(), entry.z());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverCard(int mouseX, int mouseY, Rect2i bounds) {
        return mouseX >= bounds.getX() && mouseX < bounds.getX() + bounds.getWidth()
                && mouseY >= bounds.getY() && mouseY < bounds.getY() + bounds.getHeight();
    }

    private Rect2i getCardBounds(int visibleIndex) {
        return new Rect2i(
                getOrZero(this.anchorCardsStyle.getLeft()),
                getOrZero(this.anchorCardsStyle.getTop()) + visibleIndex * (this.anchorCardsStyle.getHeight() + ACTION_BUTTON_GAP),
                this.anchorCardsStyle.getWidth(),
                this.anchorCardsStyle.getHeight()
        );
    }

    private void updateFixedScrollbarLayout() {
        this.scrollbar.setPosition(new Point(
                getOrZero(this.scrollbarStyle.getLeft()),
                getOrZero(this.scrollbarStyle.getTop())));
        this.scrollbar.setHeight(this.scrollbarStyle.getHeight());
    }

    private void drawNineSlicedTexture(GuiGraphics guiGraphics, ResourceLocation texture, Rect2i bounds,
            int textureWidth, int textureHeight, int left, int top, int right, int bottom) {
        int centerDstWidth = Math.max(0, bounds.getWidth() - left - right);
        int centerDstHeight = Math.max(0, bounds.getHeight() - top - bottom);
        int x = bounds.getX();
        int y = bounds.getY();
        int width = bounds.getWidth();
        int height = bounds.getHeight();

        guiGraphics.blit(texture, x, y, 0, 0, 0, left, top, textureWidth, textureHeight);
        guiGraphics.blit(texture, x + width - right, y, 0,
                textureWidth - right, 0, right, top, textureWidth, textureHeight);
        guiGraphics.blit(texture, x, y + height - bottom, 0,
                0, textureHeight - bottom, left, bottom, textureWidth, textureHeight);
        guiGraphics.blit(texture, x + width - right, y + height - bottom, 0,
                textureWidth - right, textureHeight - bottom, right, bottom, textureWidth, textureHeight);

        if (centerDstWidth > 0) {
            guiGraphics.blit(texture, x + left, y, 0,
                    left, 0, centerDstWidth, top, textureWidth, textureHeight);
            guiGraphics.blit(texture, x + left, y + height - bottom, 0,
                    left, textureHeight - bottom, centerDstWidth, bottom, textureWidth, textureHeight);
        }

        if (centerDstHeight > 0) {
            guiGraphics.blit(texture, x, y + top, 0,
                    0, top, left, centerDstHeight, textureWidth, textureHeight);
            guiGraphics.blit(texture, x + width - right, y + top, 0,
                    textureWidth - right, top, right, centerDstHeight, textureWidth, textureHeight);
        }

        if (centerDstWidth > 0 && centerDstHeight > 0) {
            guiGraphics.blit(texture, x + left, y + top, 0,
                    left, top, centerDstWidth, centerDstHeight, textureWidth, textureHeight);
        }
    }

    private void drawScaledCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int y, int color, float scale) {
        String value = text.getString();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        int scaledWidth = this.font.width(value);
        int drawX = Math.round((centerX - (scaledWidth * scale) / 2.0F) / scale);
        int drawY = Math.round(y / scale);
        guiGraphics.drawString(this.font, value, drawX, drawY, color, false);
        poseStack.popPose();
    }

    private void drawScaledLeftAlignedText(GuiGraphics guiGraphics, Component text, int x, int y, int color, float scale) {
        String value = text.getString();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        int drawX = Math.round(x / scale);
        int drawY = Math.round(y / scale);
        guiGraphics.drawString(this.font, value, drawX, drawY, color, false);
        poseStack.popPose();
    }

    private void drawCardText(GuiGraphics guiGraphics, Rect2i bounds, AnchorEntry entry) {
        var namePos = this.anchorCardNameStyle.resolve(bounds);
        var coordsPos = this.anchorCardCoordsStyle.resolve(bounds);
        var dimensionPos = this.anchorCardDimensionStyle.resolve(bounds);

        drawScaledLeftAlignedText(guiGraphics, Component.literal(entry.name()),
                namePos.getX(), namePos.getY(), 0xE7E7E7, CARD_NAME_SCALE);
        drawScaledLeftAlignedText(guiGraphics,
                Component.literal("[" + entry.x() + ", " + entry.y() + ", " + entry.z() + "]"),
                coordsPos.getX(), coordsPos.getY(), 0xC8D0D8, CARD_META_SCALE);
        drawScaledLeftAlignedText(guiGraphics, Component.literal(entry.dimensionId()),
                dimensionPos.getX(), dimensionPos.getY(), 0x9FA8B3, CARD_META_SCALE);
    }

    private int getOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void refreshAnchorEntries() {
        this.anchorEntries.clear();
        if (!this.menu.availableAnchors.isBlank()) {
            String[] lines = this.menu.availableAnchors.split("\n");
            for (String line : lines) {
                AnchorEntry entry = parseAnchorEntry(line);
                if (entry != null) {
                    this.anchorEntries.add(entry);
                }
            }
        }

        this.scrollbar.setRange(0, Math.max(0, this.anchorEntries.size() - VISIBLE_BUTTON_COUNT), 1);
        this.scrollbar.setVisible(true);
    }

    private AnchorEntry parseAnchorEntry(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        List<String> parts = splitEscaped(raw);
        if (parts.size() != 5) {
            return null;
        }

        try {
            return new AnchorEntry(
                    unescape(parts.get(0)),
                    unescape(parts.get(1)),
                    Integer.parseInt(parts.get(2)),
                    Integer.parseInt(parts.get(3)),
                    Integer.parseInt(parts.get(4)));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private List<String> splitEscaped(String raw) {
        ArrayList<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (escaping) {
                current.append('\\').append(ch);
                escaping = false;
                continue;
            }
            if (ch == '\\') {
                escaping = true;
                continue;
            }
            if (ch == '|') {
                parts.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        if (escaping) {
            current.append('\\');
        }
        parts.add(current.toString());
        return parts;
    }

    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                result.append(switch (ch) {
                    case 'n' -> '\n';
                    case 'p' -> '|';
                    case '\\' -> '\\';
                    default -> ch;
                });
                escaping = false;
                continue;
            }
            if (ch == '\\') {
                escaping = true;
                continue;
            }
            result.append(ch);
        }
        if (escaping) {
            result.append('\\');
        }
        return result.toString();
    }

    private record AnchorEntry(String name, String dimensionId, int x, int y, int z) {
    }
}
