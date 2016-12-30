package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * Created by CF on 2016-12-17.
 */
public class SidedItemHandlerWrapper implements IItemHandler {
    private int[] slots;
    private SidedItemHandler handler;
    private EnumFacing side;

    public SidedItemHandlerWrapper(SidedItemHandler handler, EnumFacing side) {
        this.slots = (this.handler = handler).getSlotsForFace(this.side = side);
    }

    @Override
    public int getSlots() {
        return this.slots.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.handler.getStackInSlot(this.slots[slot]);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return this.handler.insertItem(this.slots[slot], stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.handler.extractItem(this.slots[slot], amount, simulate);
    }

//    @Override
//    public int getSlotLimit(int slot) {
//        return this.handler.getSlotLimit(this.slots[slot]);
//    }
}
