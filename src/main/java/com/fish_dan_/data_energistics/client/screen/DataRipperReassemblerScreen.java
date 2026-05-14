package com.fish_dan_.data_energistics.client.screen;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ProgressBar.Direction;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;
import com.fish_dan_.data_energistics.registry.ModBlocks;
import com.fish_dan_.data_energistics.client.gui.DataEnergisticsIcon;
import com.fish_dan_.data_energistics.menu.DataRipperReassemblerMenu;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.client.gui.subgui.OutputSideConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DataRipperReassemblerScreen extends UpgradeableScreen<DataRipperReassemblerMenu> {
    private final ProgressBar progressBar;
    private final ServerSettingToggleButton<YesNo> autoExportButton;
    private final ActionEPPButton outputSideButton;

    public DataRipperReassemblerScreen(DataRipperReassemblerMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.autoExportButton = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(this.autoExportButton);
        this.outputSideButton = new ActionEPPButton(button -> openOutputConfig(), EPPIcon.OUTPUT_SIDES);
        this.outputSideButton.setMessage(Component.translatable("gui.extendedae.set_output_sides.open"));
        this.addToLeftToolbar(this.outputSideButton);
        this.progressBar = new ProgressBar(this.menu, style.getImage("progressBar"), Direction.VERTICAL);
        widgets.add("progressBar", this.progressBar);
    }

    private void openOutputConfig() {
        if (this.menu.getHost() == null) {
            return;
        }

        this.switchToScreen(new OutputSideConfig<>(
                this,
                new ItemStack(ModBlocks.DATA_RIPPER_REASSEMBLER.get()),
                this.menu.getHost(),
                this.menu.getOutputSides(),
                this.menu::sendSetOutputSide
        ));
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.progressBar.visible = this.menu.getMaxProgress() > 0;
        if (this.progressBar.visible) {
            int percent = this.menu.getCurrentProgress() * 100 / Math.max(1, this.menu.getMaxProgress());
            this.progressBar.setFullMsg(Component.literal(percent + "%"));
        }
        this.autoExportButton.set(this.menu.getAutoExport());
        this.outputSideButton.setVisibility(this.autoExportButton.getCurrentValue() == YesNo.YES);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && isEmptyGenericSlot(this.hoveredSlot)) {
            var semantic = this.menu.getSlotSemantic(this.hoveredSlot);
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(getEmptySlotTooltip(semantic));
            tooltip.add(getAmountTooltip(semantic, 0));
            this.drawTooltip(guiGraphics, mouseX, mouseY, tooltip);
            return;
        }

        if (this.menu.getCarried().isEmpty() && isGenericStorageSlot(this.hoveredSlot)) {
            List<Component> tooltip = new ArrayList<>(this.getTooltipFromContainerItem(this.hoveredSlot.getItem()));
            GenericStack stack = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            long amount = stack != null ? stack.amount() : 0L;
            tooltip.add(getAmountTooltip(this.menu.getSlotSemantic(this.hoveredSlot), amount));
            this.drawTooltip(guiGraphics, mouseX, mouseY, tooltip);
            return;
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (slot.isActive()
                && slot.getItem().isEmpty()
                && this.menu.getSlotSemantic(slot) == SlotSemantics.UPGRADE) {
            DataEnergisticsIcon.getBlitter("PLACEMENT_TOOLBOX")
                    .dest(slot.x, slot.y)
                    .blit(guiGraphics);
        }
        super.renderSlot(guiGraphics, slot);
    }

    private boolean isEmptyGenericSlot(Slot slot) {
        if (slot == null || !slot.isActive() || !slot.getItem().isEmpty()) {
            return false;
        }

        var semantic = this.menu.getSlotSemantic(slot);
        return semantic == SlotSemantics.STORAGE
                || semantic == DataRipperReassemblerMenu.FLUID_INPUT_B
                || semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_A
                || semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_B
                || semantic == DataRipperReassemblerMenu.KEY_INPUT
                || semantic == DataRipperReassemblerMenu.KEY_OUTPUT;
    }

    private boolean isGenericStorageSlot(Slot slot) {
        if (slot == null || !slot.isActive() || slot.getItem().isEmpty()) {
            return false;
        }

        var semantic = this.menu.getSlotSemantic(slot);
        return semantic == SlotSemantics.STORAGE
                || semantic == DataRipperReassemblerMenu.FLUID_INPUT_B
                || semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_A
                || semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_B
                || semantic == DataRipperReassemblerMenu.KEY_INPUT
                || semantic == DataRipperReassemblerMenu.KEY_OUTPUT;
    }

    private Component getEmptySlotTooltip(appeng.menu.SlotSemantic semantic) {
        if (semantic == DataRipperReassemblerMenu.KEY_INPUT || semantic == DataRipperReassemblerMenu.KEY_OUTPUT) {
            return Component.translatable("screen.data_energistics.data_reassembler.key.empty");
        }
        return Component.translatable("screen.data_energistics.data_reassembler.fluid.empty");
    }

    private Component getAmountTooltip(appeng.menu.SlotSemantic semantic, long amount) {
        if (semantic == SlotSemantics.STORAGE || semantic == DataRipperReassemblerMenu.FLUID_INPUT_B) {
            return Component.literal(amount + " mB / " + this.menu.getFluidInputCapacity() + " mB")
                    .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
        }
        if (semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_A
                || semantic == DataRipperReassemblerMenu.FLUID_OUTPUT_B) {
            return Component.literal(amount + " mB / " + this.menu.getFluidOutputCapacity() + " mB")
                    .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
        }
        if (semantic == DataRipperReassemblerMenu.KEY_INPUT) {
            return Component.literal(amount + " / " + this.menu.getKeyInputCapacity())
                    .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
        }
        return Component.literal(amount + " / " + this.menu.getKeyOutputCapacity())
                .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT);
    }
}
