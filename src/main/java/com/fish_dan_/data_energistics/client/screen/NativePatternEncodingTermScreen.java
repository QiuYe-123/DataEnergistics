package com.fish_dan_.data_energistics.client.screen;

import appeng.api.client.AEKeyRendering;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.localization.Tooltips;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.encoding.PatternEncodingLogic;
import appeng.util.ConfigInventory;
import com.fish_dan_.data_energistics.client.GenericStackDisplayHelper;
import com.fish_dan_.data_energistics.menu.common.PatternEncodingTransferKeyAware;
import com.fish_dan_.data_energistics.recipe.DataRipperReassemblerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NativePatternEncodingTermScreen extends PatternEncodingPreviewScreen<PatternEncodingTermMenu> {
    private static final int AE2_PREVIEW_PANEL_Y_OFFSET = 105;
    private static final int AE2_PREVIEW_SCROLLBAR_SCREEN_X = 309;
    private static final int AE2_PREVIEW_SCROLLBAR_SCREEN_Y = 121;
    private static final int AE2_PREVIEW_SCROLLBAR_HEIGHT = 104;
    private static final int AE2_SEARCH_BOX_X = 42;
    private static final int AE2_SEARCH_BOX_Y = 6;
    private static final int MAX_VISIBLE_ITEM_INPUT_SLOTS = 9;

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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (Minecraft.getInstance().options.keyPickItem.matchesMouse(button)) {
            Slot keySlot = getVisualKeySlot();
            GenericStack keyInput = getEncodedKeyInput();
            if (keySlot != null
                    && keyInput != null
                    && isMouseOverVisualSlot(keySlot, mouseX, mouseY)) {
                SetIntProcessingPatternAmountScreen screen =
                        new SetIntProcessingPatternAmountScreen(this, keyInput, newStack -> {
                            if (this.menu instanceof PatternEncodingTransferKeyAware transferKeyAware) {
                                transferKeyAware.dataEnergistics$sendTransferKeyInputAction(
                                        serializeTransferKeyInput(newStack));
                            }
                            Slot actualKeySlot = getActualEncodedKeySlot();
                            if (actualKeySlot == null) {
                                return;
                            }
                            ServerboundPacket message = new InventoryActionPacket(
                                    InventoryAction.SET_FILTER,
                                    actualKeySlot.index,
                                    GenericStack.wrapInItemStack(newStack));
                            PacketDistributor.sendToServer(message, new CustomPacketPayload[0]);
                        });
                this.switchToScreen(screen);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Slot keySlot = getVisualKeySlot();
        GenericStack keyInput = getEncodedKeyInput();
        if (keySlot != null
                && keyInput != null
                && this.hoveredSlot == keySlot
                && mouseX >= this.leftPos + keySlot.x
                && mouseX < this.leftPos + keySlot.x + 16
                && mouseY >= this.topPos + keySlot.y
                && mouseY < this.topPos + keySlot.y + 16) {
            List<FormattedCharSequence> lines = List.of(
                    keyInput.what().getDisplayName(),
                    GenericStackDisplayHelper.createAmountTooltip(keyInput),
                    Tooltips.getSetAmountTooltip()
            ).stream().map(Component::getVisualOrderText).toList();
            guiGraphics.renderTooltip(this.font, lines, mouseX, mouseY);
            return;
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);

        Slot keySlot = getVisualKeySlot();
        GenericStack keyInput = getEncodedKeyInput();
        if (slot != keySlot || keyInput == null || !slot.getItem().isEmpty()) {
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, slot.x, slot.y, keyInput.what());
        GenericStackDisplayHelper.renderSmallOverlay(
                guiGraphics,
                slot.x,
                slot.y,
                GenericStackDisplayHelper.formatCompactAmount(keyInput));
        guiGraphics.pose().popPose();
    }

    private Slot getVisualKeySlot() {
        int occupiedItemSlots = 0;
        if (this.menu.getHost() instanceof IPatternTerminalMenuHost host) {
            PatternEncodingLogic logic = host.getLogic();
            ConfigInventory encodedInputsInv = logic.getEncodedInputInv();
            int limit = Math.min(MAX_VISIBLE_ITEM_INPUT_SLOTS, encodedInputsInv.size());
            for (int i = 0; i < limit; i++) {
                GenericStack stack = encodedInputsInv.getStack(i);
                if (stack != null && stack.what() instanceof appeng.api.stacks.AEItemKey) {
                    occupiedItemSlots++;
                }
            }
        }

        if (occupiedItemSlots >= MAX_VISIBLE_ITEM_INPUT_SLOTS) {
            return null;
        }

        List<Slot> processingInputSlots = getVisibleProcessingInputSlots();
        if (processingInputSlots.size() <= occupiedItemSlots) {
            return null;
        }

        return processingInputSlots.get(occupiedItemSlots);
    }

    private List<Slot> getVisibleProcessingInputSlots() {
        var slots = new ArrayList<>(this.menu.getSlots(SlotSemantics.PROCESSING_INPUTS));
        slots.sort(Comparator
                .comparingInt((Slot slot) -> slot.y)
                .thenComparingInt(slot -> slot.x));
        if (slots.size() > MAX_VISIBLE_ITEM_INPUT_SLOTS) {
            return slots.subList(0, MAX_VISIBLE_ITEM_INPUT_SLOTS);
        }
        return slots;
    }

    private GenericStack getEncodedKeyInput() {
        if (!(this.menu.getHost() instanceof IPatternTerminalMenuHost host)) {
            return null;
        }

        PatternEncodingLogic logic = host.getLogic();
        ConfigInventory encodedInputsInv = logic.getEncodedInputInv();
        int slot = DataRipperReassemblerRecipe.KEY_INPUT_SLOT_INDEX;
        if (slot < 0 || slot >= encodedInputsInv.size()) {
            return null;
        }
        return encodedInputsInv.getStack(slot);
    }

    private boolean isMouseOverVisualSlot(Slot slot, double mouseX, double mouseY) {
        return mouseX >= this.leftPos + slot.x
                && mouseX < this.leftPos + slot.x + 16
                && mouseY >= this.topPos + slot.y
                && mouseY < this.topPos + slot.y + 16;
    }

    private Slot getActualEncodedKeySlot() {
        var slots = this.menu.getSlots(SlotSemantics.PROCESSING_INPUTS);
        int index = DataRipperReassemblerRecipe.KEY_INPUT_SLOT_INDEX;
        if (index < 0 || index >= slots.size()) {
            return null;
        }
        return slots.get(index);
    }

    private String serializeTransferKeyInput(GenericStack stack) {
        return GenericStack.writeTag(this.minecraft.player.registryAccess(), stack).toString();
    }
}
