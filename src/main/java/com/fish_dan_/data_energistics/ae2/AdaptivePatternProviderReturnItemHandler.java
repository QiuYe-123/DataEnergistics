package com.fish_dan_.data_energistics.ae2;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class AdaptivePatternProviderReturnItemHandler implements IItemHandler {
    private final Supplier<@Nullable AdaptivePatternProviderLogic> logicSupplier;

    public AdaptivePatternProviderReturnItemHandler(Supplier<@Nullable AdaptivePatternProviderLogic> logicSupplier) {
        this.logicSupplier = Objects.requireNonNull(logicSupplier, "logicSupplier");
    }

    @Override
    public int getSlots() {
        AdaptivePatternProviderLogic logic = this.logicSupplier.get();
        return logic != null ? logic.getReturnInventorySlotCount() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        AdaptivePatternProviderLogic logic = this.logicSupplier.get();
        return logic != null ? logic.getReturnInventoryStack(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        AdaptivePatternProviderLogic logic = this.logicSupplier.get();
        return logic != null ? logic.insertReturnInventoryItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty() && !ItemStack.matches(stack, insertItem(slot, stack.copy(), true));
    }
}
