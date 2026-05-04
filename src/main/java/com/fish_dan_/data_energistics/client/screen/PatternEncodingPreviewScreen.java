package com.fish_dan_.data_energistics.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import appeng.client.Point;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.common.StackSizeRenderer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.helpers.InventoryAction;
import appeng.core.definitions.AEItems;
import appeng.api.stacks.AEItemKey;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.util.ReadableNumberConverter;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingPreviewMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

public class PatternEncodingPreviewScreen<T extends PatternEncodingTermMenu> extends PatternEncodingTermScreen<T> {
    private static final Field WIDGET_CONTAINER_WIDGETS_FIELD = resolveField(WidgetContainer.class, "widgets");
    private static final ResourceLocation AE2_UPLOAD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/guis/upload.png");
    private static final ResourceLocation AE2_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/gui/sprites/button.png");
    private static final ResourceLocation AE2_BUTTON_HIGHLIGHTED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/gui/sprites/button_highlighted.png");
    private static final ResourceLocation AE2_BUTTON_DISABLED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ae2", "textures/gui/sprites/button_disabled.png");
    private static final Component PANEL_TITLE = Component.literal("上传");
    private static final int COLOR_PANEL_TITLE = 0x000000;
    private static final Component EMPTY_STATE_TEXT = Component.literal("未发现可用供应器");
    private static final Component ENCODE_BUTTON_HINT = Component.literal(
            "编写样板\n右键时打开上传面板\nShift+右键时关闭上传面板");
    private static final int PREVIEW_PANEL_WIDTH = 128;
    private static final int PREVIEW_PANEL_HEIGHT = 128;
    private static final int PREVIEW_TEXTURE_WIDTH = 128;
    private static final int PREVIEW_TEXTURE_HEIGHT = 128;
    private static final int PREVIEW_TEXTURE_U = 0;
    private static final int PREVIEW_TEXTURE_V = 0;
    private static final int BUTTON_TEXTURE_WIDTH = 200;
    private static final int BUTTON_TEXTURE_HEIGHT = 20;
    private static final int BUTTON_SLICE_BORDER = 4;
    private static final int PREVIEW_PANEL_MARGIN = 0;
    private static final int PREVIEW_PANEL_X_OFFSET = 0;
    private static final int PREVIEW_PANEL_Y_OFFSET = 105;
    private static final int PREVIEW_SCROLLBAR_SCREEN_X = 309;
    private static final int PREVIEW_SCROLLBAR_SCREEN_Y = 121;
    private static final int PREVIEW_SCROLLBAR_HEIGHT = 104;
    private static final int PANEL_CONTENT_X = 10;
    private static final int PANEL_CONTENT_RIGHT = 6;
    private static final int PANEL_CONTENT_BOTTOM = 6;
    private static final int PANEL_TITLE_Y = 4;
    private static final int SEARCH_BOX_X = 42;
    private static final int SEARCH_BOX_Y = 6;
    private static final int SEARCH_BOX_WIDTH = 70;
    private static final int SEARCH_BOX_HEIGHT = 12;
    private static final int PROVIDER_LIST_Y = 20;
    private static final int PROVIDER_BUTTON_GAP = -1;
    private static final int PROVIDER_VISIBLE_ROWS = 5;
    private static final int PROVIDER_BUTTON_WIDTH = 95;
    private static final int PROVIDER_BUTTON_HEIGHT = 20;
    private static final int PROVIDER_NAME_X_PADDING = 4;
    private static final int PROVIDER_ICON_SIZE = 16;
    private static final int PROVIDER_ICON_X_PADDING = 2;
    private static final int PROVIDER_COUNT_RIGHT_PADDING = 4;
    private static final int PROVIDER_TEXT_Y_OFFSET = 5;
    private static final float PROVIDER_TEXT_SCALE = 0.75F;
    private static final float PROVIDER_COUNT_TEXT_SCALE = 0.68F;
    private static final int COLOR_PANEL_TEXT = 0xE7E7E7;
    private static final int COLOR_EMPTY_STATE_TEXT = 0x000000;
    private static final int COLOR_PANEL_MUTED = 0x9C9C9C;
    private static final int COLOR_PROVIDER_COUNT_NORMAL = 0x9CD3FF;
    private static final int COLOR_PROVIDER_COUNT_WARNING = 0xC83A32;
    private static final int COLOR_BUTTON = 0x6A111111;
    private static final int COLOR_BUTTON_HOVER = 0x88333333;
    private static final int COLOR_BUTTON_SELECTED = 0xAA5F7991;
    private static final int COLOR_BUTTON_BORDER = 0xB0909090;
    private static final Component SEARCH_BOX_HINT = Component.literal("搜索");

    private boolean previewVisible;
    private boolean previewScrollbarDragging;
    private long selectedPatternProviderId = -1L;
    private final Scrollbar previewScrollbar = new Scrollbar(Scrollbar.SMALL);
    private AbstractWidget encodePatternWidget;
    private AETextField providerSearchBox;

    public PatternEncodingPreviewScreen(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.previewScrollbar.setCaptureMouseWheel(false);
        this.previewScrollbar.setRange(0, 0, 1);
    }

    @Override
    public void init() {
        super.init();
        this.encodePatternWidget = resolveEncodePatternWidget();
        applyEncodeButtonHint();
        initProviderSearchBox();
        updateProviderSearchBox();
        updatePreviewScrollbar();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        updateProviderSearchBox();
        syncProviderSelection();
        updatePreviewScrollbar();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Minecraft.getInstance().options.keyPickItem.matchesMouse(button)
                && triggerBlankPatternAutoCraft(mouseX, mouseY)) {
            return true;
        }

        if (button == 1 && isOverEncodeButton(mouseX, mouseY)) {
            if (this.previewVisible) {
                if (hasShiftDown()) {
                    this.previewVisible = false;
                } else {
                    this.menu.encode();
                }
            } else {
                this.previewVisible = true;
                this.menu.encode();
            }
            return true;
        }

        if (this.previewVisible && isOverPreviewScrollbar(mouseX, mouseY)) {
            boolean handled = this.previewScrollbar.onMouseDown(
                    new Point((int) Math.round(mouseX), (int) Math.round(mouseY)), button);
            if (handled) {
                this.previewScrollbarDragging = true;
                return true;
            }
        }

        if (this.previewVisible && button == 0) {
            var hit = getProviderButtonHit(mouseX, mouseY);
            if (hit != null) {
                this.selectedPatternProviderId = hit.provider().id();
                return true;
            }
        }

        if (this.previewVisible && button == 1) {
            var hit = getProviderButtonHit(mouseX, mouseY);
            if (hit != null) {
                this.selectedPatternProviderId = hit.provider().id();
                previewBridge().transferEncodedPatternToProvider(hit.provider().id());
                return true;
            }
        }

        boolean shouldClosePreviewAfterEncode = button == 0 && this.previewVisible && isOverEncodeButton(mouseX, mouseY);
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (shouldClosePreviewAfterEncode && handled) {
            this.previewVisible = false;
            return true;
        }

        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.previewScrollbarDragging) {
            this.previewScrollbar.onMouseUp(new Point((int) Math.round(mouseX), (int) Math.round(mouseY)), button);
            this.previewScrollbarDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        if (this.previewVisible && this.previewScrollbarDragging
                && this.previewScrollbar.onMouseDrag(new Point((int) Math.round(mouseX), (int) Math.round(mouseY)),
                mouseButton)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.previewVisible && (isOverPreviewScrollbar(mouseX, mouseY) || isOverProviderList(mouseX, mouseY))
                && this.previewScrollbar.onMouseWheel(new Point((int) Math.round(mouseX), (int) Math.round(mouseY)),
                scrollY)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY,
                       float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        if (!this.previewVisible) {
            return;
        }

        Rect2i previewBounds = getPreviewPanelBounds();
        guiGraphics.blit(AE2_UPLOAD_TEXTURE,
                previewBounds.getX(), previewBounds.getY(),
                0,
                PREVIEW_TEXTURE_U, PREVIEW_TEXTURE_V,
                previewBounds.getWidth(), previewBounds.getHeight(),
                PREVIEW_TEXTURE_WIDTH, PREVIEW_TEXTURE_HEIGHT);

        drawProviderButtons(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        if (!this.previewVisible) {
            return;
        }

        this.previewScrollbar.drawForegroundLayer(guiGraphics, getPreviewPanelBounds(), new Point(mouseX, mouseY));
        renderProviderTooltips(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        if (this.menu.getSlotSemantic(slot) != SlotSemantics.BLANK_PATTERN) {
            return;
        }

        long networkCount = previewBridge().getNetworkBlankPatternCount();
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

        if (isBlankPatternCraftable()) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 100.0F);
            StackSizeRenderer.renderSizeLabel(guiGraphics, this.font, (float) (slot.x - 11), (float) (slot.y - 11),
                    "+", false);
            poseStack.popPose();
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

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.previewVisible) {
            this.previewScrollbar.tick();
        }
    }

    private PatternEncodingPreviewMenu previewBridge() {
        if (this.menu instanceof PatternEncodingPreviewMenu bridge) {
            return bridge;
        }
        throw new IllegalStateException("Pattern encoding menu does not implement preview bridge: " + this.menu.getClass().getName());
    }

    private void drawProviderButtons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Rect2i previewBounds = getPreviewPanelBounds();
        guiGraphics.drawString(this.font, PANEL_TITLE,
                previewBounds.getX() + PANEL_CONTENT_X,
                previewBounds.getY() + PANEL_TITLE_Y,
                COLOR_PANEL_TITLE,
                false);

        List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers = previewBridge().getSyncedPatternProviders();
        List<PatternEncodingPreviewMenu.SyncedPatternProvider> visibleProviders = getVisibleProviders(providers);
        if (visibleProviders.isEmpty()) {
            drawScaledText(guiGraphics, EMPTY_STATE_TEXT.getString(),
                    previewBounds.getX() + PANEL_CONTENT_X,
                    previewBounds.getY() + PROVIDER_LIST_Y + 2 + PROVIDER_TEXT_Y_OFFSET,
                    COLOR_EMPTY_STATE_TEXT,
                    PROVIDER_TEXT_SCALE);
            return;
        }

        int start = this.previewScrollbar.getCurrentScroll();
        int end = Math.min(visibleProviders.size(), start + PROVIDER_VISIBLE_ROWS);
        for (int rowIndex = start; rowIndex < end; rowIndex++) {
            var provider = visibleProviders.get(rowIndex);
            int visibleRow = rowIndex - start;
            Rect2i providerButtonBounds = getProviderButtonBounds(visibleRow);
            boolean selected = provider.id() == this.selectedPatternProviderId;
            boolean hoveredProvider = providerButtonBounds.contains(mouseX, mouseY);

            drawProviderButtonBackground(guiGraphics, providerButtonBounds, provider, selected, hoveredProvider);

            ItemStack providerIcon = getProviderIconStack(provider);
            int nameStartX = providerButtonBounds.getX() + PROVIDER_NAME_X_PADDING;
            if (!providerIcon.isEmpty()) {
                int iconX = providerButtonBounds.getX() + PROVIDER_ICON_X_PADDING;
                int iconY = providerButtonBounds.getY() + (providerButtonBounds.getHeight() - PROVIDER_ICON_SIZE) / 2;
                guiGraphics.renderItem(providerIcon, iconX, iconY);
                nameStartX = iconX + PROVIDER_ICON_SIZE + 2;
            }

            String countText = provider.usedPatternSlotCount() + "/" + provider.patternSlotCount();
            int countWidth = getScaledTextWidth(countText, PROVIDER_COUNT_TEXT_SCALE);
            int maxNameWidth = providerButtonBounds.getX() + providerButtonBounds.getWidth()
                    - PROVIDER_COUNT_RIGHT_PADDING - countWidth - 4 - nameStartX;
            String providerName = trimToWidth(provider.displayName().getString(), Math.max(10, maxNameWidth),
                    PROVIDER_TEXT_SCALE);

            drawScaledText(guiGraphics, providerName,
                    nameStartX,
                    providerButtonBounds.getY() + 2 + PROVIDER_TEXT_Y_OFFSET,
                    COLOR_PANEL_TEXT,
                    PROVIDER_TEXT_SCALE);
            drawScaledText(guiGraphics, countText,
                    providerButtonBounds.getX() + providerButtonBounds.getWidth() - PROVIDER_COUNT_RIGHT_PADDING - countWidth,
                    providerButtonBounds.getY() + 2 + PROVIDER_TEXT_Y_OFFSET,
                    getProviderCountColor(provider),
                    PROVIDER_COUNT_TEXT_SCALE);
        }
    }

    private void renderProviderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var buttonHit = getProviderButtonHit(mouseX, mouseY);
        if (buttonHit == null) {
            return;
        }

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(buttonHit.provider().displayName());
        tooltip.add(Component.literal("已写入 "
                + buttonHit.provider().usedPatternSlotCount()
                + " / "
                + buttonHit.provider().patternSlotCount()));
        List<FormattedCharSequence> lines = tooltip.stream()
                .map(Component::getVisualOrderText)
                .toList();
        guiGraphics.renderTooltip(this.font, lines, mouseX + 6, mouseY + 4);
    }

    private void updatePreviewScrollbar() {
        int hiddenRows = Math.max(0, getVisibleProviders(previewBridge().getSyncedPatternProviders()).size() - PROVIDER_VISIBLE_ROWS);
        this.previewScrollbar.setPosition(new Point(
                PREVIEW_SCROLLBAR_SCREEN_X,
                PREVIEW_SCROLLBAR_SCREEN_Y));
        this.previewScrollbar.setHeight(PREVIEW_SCROLLBAR_HEIGHT);
        this.previewScrollbar.setRange(0, hiddenRows, 1);
        this.previewScrollbar.setVisible(this.previewVisible && hiddenRows > 0);
        this.previewScrollbar.setCurrentScroll(Math.min(this.previewScrollbar.getCurrentScroll(), hiddenRows));
    }

    private void syncProviderSelection() {
        List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers =
                getVisibleProviders(previewBridge().getSyncedPatternProviders());
        if (providers.isEmpty()) {
            this.selectedPatternProviderId = -1L;
            return;
        }

        if (getPatternProvider(this.selectedPatternProviderId) == null) {
            this.selectedPatternProviderId = providers.getFirst().id();
        }
    }

    private PatternEncodingPreviewMenu.SyncedPatternProvider getPatternProvider(long providerId) {
        for (var provider : getVisibleProviders(previewBridge().getSyncedPatternProviders())) {
            if (provider.id() == providerId) {
                return provider;
            }
        }
        return null;
    }

    private ProviderButtonHit getProviderButtonHit(double mouseX, double mouseY) {
        List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers =
                getVisibleProviders(previewBridge().getSyncedPatternProviders());
        int start = this.previewScrollbar.getCurrentScroll();
        int end = Math.min(providers.size(), start + PROVIDER_VISIBLE_ROWS);
        for (int rowIndex = start; rowIndex < end; rowIndex++) {
            int visibleRow = rowIndex - start;
            var provider = providers.get(rowIndex);
            if (getProviderButtonBounds(visibleRow).contains((int) mouseX, (int) mouseY)) {
                return new ProviderButtonHit(provider);
            }
        }
        return null;
    }

    private boolean isOverEncodeButton(double mouseX, double mouseY) {
        if (this.encodePatternWidget != null && this.encodePatternWidget.visible) {
            return this.encodePatternWidget.isMouseOver(mouseX, mouseY);
        }

        WidgetStyle buttonStyle = this.getStyle().getWidget("encodePattern");
        Point position = buttonStyle.resolve(new Rect2i(this.leftPos, this.topPos, this.imageWidth, this.imageHeight));
        int width = buttonStyle.getWidth() > 0 ? buttonStyle.getWidth() : 16;
        int height = buttonStyle.getHeight() > 0 ? buttonStyle.getHeight() : 16;
        return mouseX >= position.getX() && mouseX < position.getX() + width
                && mouseY >= position.getY() && mouseY < position.getY() + height;
    }

    @SuppressWarnings("unchecked")
    private AbstractWidget resolveEncodePatternWidget() {
        try {
            Map<String, AbstractWidget> widgetsById =
                    (Map<String, AbstractWidget>) WIDGET_CONTAINER_WIDGETS_FIELD.get(this.widgets);
            return widgetsById.get("encodePattern");
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private void applyEncodeButtonHint() {
        if (this.encodePatternWidget != null) {
            this.encodePatternWidget.setMessage(ENCODE_BUTTON_HINT);
        }
    }

    private void initProviderSearchBox() {
        String currentText = this.providerSearchBox != null ? this.providerSearchBox.getValue() : "";
        this.providerSearchBox = new AETextField(this.getStyle(), this.font, 0, 0, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT);
        this.providerSearchBox.setMaxLength(40);
        this.providerSearchBox.setBordered(false);
        this.providerSearchBox.setVisible(false);
        this.providerSearchBox.setCanLoseFocus(true);
        this.providerSearchBox.setPlaceholder(SEARCH_BOX_HINT);
        this.providerSearchBox.setResponder(value -> this.previewScrollbar.setCurrentScroll(0));
        this.providerSearchBox.setValue(currentText);
        this.addRenderableWidget(this.providerSearchBox);
    }

    private void updateProviderSearchBox() {
        if (this.providerSearchBox == null) {
            return;
        }

        Rect2i previewBounds = getPreviewPanelBounds();
        this.providerSearchBox.setX(previewBounds.getX() + SEARCH_BOX_X);
        this.providerSearchBox.setY(previewBounds.getY() + SEARCH_BOX_Y);
        this.providerSearchBox.setVisible(this.previewVisible);
        this.providerSearchBox.active = this.previewVisible;
        if (!this.previewVisible) {
            this.providerSearchBox.setFocused(false);
        }
    }

    private boolean triggerBlankPatternAutoCraft(double mouseX, double mouseY) {
        Slot slot = this.hoveredSlot;
        if (slot == null || this.menu.getSlotSemantic(slot) != SlotSemantics.BLANK_PATTERN) {
            return false;
        }
        if (!isMouseOverSlot(slot, mouseX, mouseY)) {
            return false;
        }

        GridInventoryEntry blankPatternEntry = findBlankPatternEntry();
        if (blankPatternEntry == null || !blankPatternEntry.isCraftable()) {
            return false;
        }

        this.menu.handleInteraction(blankPatternEntry.getSerial(), InventoryAction.AUTO_CRAFT);
        return true;
    }

    private GridInventoryEntry findBlankPatternEntry() {
        AEItemKey blankPatternKey = AEItemKey.of(AEItems.BLANK_PATTERN);
        if (blankPatternKey == null) {
            return null;
        }

        for (GridInventoryEntry entry : this.repo.getAllEntries()) {
            if (entry.isCraftable() && blankPatternKey.equals(entry.getWhat())) {
                return entry;
            }
        }

        return null;
    }

    private boolean isBlankPatternCraftable() {
        GridInventoryEntry blankPatternEntry = findBlankPatternEntry();
        return blankPatternEntry != null && blankPatternEntry.isCraftable();
    }

    private boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
        return mouseX >= this.leftPos + slot.x
                && mouseX < this.leftPos + slot.x + 16
                && mouseY >= this.topPos + slot.y
                && mouseY < this.topPos + slot.y + 16;
    }

    private List<PatternEncodingPreviewMenu.SyncedPatternProvider> getVisibleProviders(
            List<PatternEncodingPreviewMenu.SyncedPatternProvider> providers) {
        String query = this.providerSearchBox != null ? this.providerSearchBox.getValue().trim().toLowerCase() : "";
        if (query.isEmpty()) {
            return providers;
        }

        List<PatternEncodingPreviewMenu.SyncedPatternProvider> filtered = new ArrayList<>();
        for (var provider : providers) {
            String displayName = provider.displayName().getString().toLowerCase();
            String iconName = provider.iconItemId().toString().toLowerCase();
            if (displayName.contains(query) || iconName.contains(query)) {
                filtered.add(provider);
            }
        }
        return filtered;
    }

    private static Field resolveField(Class<?> owner, String name) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not resolve field " + owner.getName() + "#" + name, e);
        }
    }

    private Rect2i getPreviewPanelBounds() {
        int preferredX = this.leftPos + this.imageWidth + PREVIEW_PANEL_MARGIN + PREVIEW_PANEL_X_OFFSET;
        if (preferredX + PREVIEW_PANEL_WIDTH > this.width - 4) {
            preferredX = this.leftPos - PREVIEW_PANEL_WIDTH - PREVIEW_PANEL_MARGIN + PREVIEW_PANEL_X_OFFSET;
        }

        int x = Math.max(4, Math.min(preferredX, this.width - PREVIEW_PANEL_WIDTH - 4));
        int preferredY = this.topPos + PREVIEW_PANEL_Y_OFFSET;
        int y = Math.max(4, Math.min(preferredY, this.height - PREVIEW_PANEL_HEIGHT - 4));
        return new Rect2i(x, y, PREVIEW_PANEL_WIDTH, PREVIEW_PANEL_HEIGHT);
    }

    private Rect2i getProviderButtonBounds(int visibleRow) {
        Rect2i listBounds = getProviderListBounds();
        int x = listBounds.getX();
        int y = listBounds.getY() + visibleRow * (PROVIDER_BUTTON_HEIGHT + PROVIDER_BUTTON_GAP);
        return new Rect2i(x, y, PROVIDER_BUTTON_WIDTH, PROVIDER_BUTTON_HEIGHT);
    }

    private boolean isOverPreviewScrollbar(double mouseX, double mouseY) {
        Rect2i bounds = this.previewScrollbar.getBounds();
        return mouseX >= bounds.getX() && mouseX < bounds.getX() + bounds.getWidth()
                && mouseY >= bounds.getY() && mouseY < bounds.getY() + bounds.getHeight();
    }

    private boolean isOverProviderList(double mouseX, double mouseY) {
        Rect2i listBounds = getProviderListBounds();
        return mouseX >= listBounds.getX() && mouseX < listBounds.getX() + listBounds.getWidth()
                && mouseY >= listBounds.getY() && mouseY < listBounds.getY() + listBounds.getHeight();
    }

    private void drawPanelButton(GuiGraphics guiGraphics, Rect2i bounds, int fillColor) {
        guiGraphics.fill(bounds.getX(), bounds.getY(),
                bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(),
                fillColor);
        guiGraphics.renderOutline(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(),
                COLOR_BUTTON_BORDER);
    }

    private void drawProviderButtonBackground(GuiGraphics guiGraphics, Rect2i bounds,
                                              PatternEncodingPreviewMenu.SyncedPatternProvider provider,
                                              boolean selected, boolean hovered) {
        if (isMePatternProvider(provider)) {
            ResourceLocation texture = selected
                    ? AE2_BUTTON_DISABLED_TEXTURE
                    : hovered ? AE2_BUTTON_HIGHLIGHTED_TEXTURE : AE2_BUTTON_TEXTURE;
            drawNineSlicedTexture(guiGraphics, texture, bounds,
                    BUTTON_TEXTURE_WIDTH, BUTTON_TEXTURE_HEIGHT,
                    BUTTON_SLICE_BORDER, BUTTON_SLICE_BORDER, BUTTON_SLICE_BORDER, BUTTON_SLICE_BORDER);
            return;
        }

        drawPanelButton(guiGraphics, bounds,
                selected ? COLOR_BUTTON_SELECTED : hovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON);
    }

    private Rect2i getProviderListBounds() {
        Rect2i panelBounds = getPreviewPanelBounds();
        int x = panelBounds.getX() + PANEL_CONTENT_X;
        int y = panelBounds.getY() + PROVIDER_LIST_Y;
        int width = panelBounds.getWidth() - PANEL_CONTENT_X - PANEL_CONTENT_RIGHT;
        int height = panelBounds.getHeight() - PROVIDER_LIST_Y - PANEL_CONTENT_BOTTOM;
        return new Rect2i(x, y, Math.max(1, width), Math.max(1, height));
    }

    private boolean isMePatternProvider(PatternEncodingPreviewMenu.SyncedPatternProvider provider) {
        return provider.useAeButtonStyle();
    }

    private void drawNineSlicedTexture(GuiGraphics guiGraphics, ResourceLocation texture, Rect2i bounds,
                                       int textureWidth, int textureHeight,
                                       int left, int top, int right, int bottom) {
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

    private void drawScaledText(GuiGraphics guiGraphics, String text, int x, int y, int color, float scale) {
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1.0F);
        guiGraphics.drawString(this.font, text,
                Math.round(x / scale),
                Math.round(y / scale),
                color,
                false);
        poseStack.popPose();
    }

    private int getScaledTextWidth(String text, float scale) {
        return (int) Math.ceil(this.font.width(text) * scale);
    }

    private int getProviderCountColor(PatternEncodingPreviewMenu.SyncedPatternProvider provider) {
        int total = provider.patternSlotCount();
        if (total <= 0) {
            return COLOR_PROVIDER_COUNT_NORMAL;
        }

        int remaining = Math.max(0, total - provider.usedPatternSlotCount());
        return remaining * 9 < total * 2 ? COLOR_PROVIDER_COUNT_WARNING : COLOR_PROVIDER_COUNT_NORMAL;
    }

    private String trimToWidth(String text, int maxWidth, float scale) {
        if (getScaledTextWidth(text, scale) <= maxWidth) {
            return text;
        }
        int ellipsisWidth = getScaledTextWidth("...", scale);
        int rawWidthLimit = Math.max(0, (int) Math.floor((maxWidth - ellipsisWidth) / scale));
        return this.font.plainSubstrByWidth(text, rawWidthLimit) + "...";
    }

    private ItemStack getProviderIconStack(PatternEncodingPreviewMenu.SyncedPatternProvider provider) {
        var item = BuiltInRegistries.ITEM.get(provider.iconItemId());
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private record ProviderButtonHit(PatternEncodingPreviewMenu.SyncedPatternProvider provider) {
    }
}
