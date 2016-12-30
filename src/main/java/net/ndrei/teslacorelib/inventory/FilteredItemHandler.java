package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;

import javax.annotation.Nonnull;

/**
 * Created by CF on 2016-12-16.
 */
public class FilteredItemHandler implements IFilteredItemHandler {
    protected final IItemHandler handler;

    protected FilteredItemHandler(IItemHandler handler) {
        if (null == (this.handler = handler)) {
            throw new RuntimeException("No inner IItemHandler provided.");
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canExtractItem(int slot) {
        return true;
    }

    @Override
    public final int getSlots() {
        return this.handler.getSlots();
    }

    @Nonnull
    @Override
    public final ItemStack getStackInSlot(int slot) {
        return this.handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public final ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.canInsertItem(slot, stack)) {
            return stack;
        }
        return this.handler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public final ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!this.canExtractItem(slot)) {
            return ItemStackUtil.getEmptyStack();
        }
        return this.handler.extractItem(slot, amount, simulate);
    }

//    @Override
//    public final int getSlotLimit(int slot) {
//        return this.handler.getSlotLimit(slot);
//    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
//        ItemStack existing = this.getStackInSlot(slot);
//        if (ItemStack.areItemStacksEqual(stack, existing)) {
//            return;
//        }

//        if (!ItemStackUtil.isEmpty(existing)) {
//            throw new RuntimeException("That slot is not empty!");
//        }

//        if (!ItemStackUtil.isEmpty(stack) && !this.canInsertItem(slot, stack)) {
//            throw new RuntimeException("That slot does not accept that stack!");
//        }

        if (this.handler instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable)this.handler).setStackInSlot(slot, stack);
        }
        else {
            throw new RuntimeException("Inner item handler is not modifiable.");
        }
    }
}
