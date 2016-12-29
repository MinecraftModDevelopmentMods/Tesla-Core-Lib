//package net.ndrei.teslacorelib.inventory;
//
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.items.IItemHandler;
//
//import javax.annotation.Nonnull;
//
///**
// * Created by CF on 2016-12-17.
// */
//abstract class ItemHandlerWrapper implements IItemHandler {
//    private IItemHandler handler = null;
//
//    protected ItemHandlerWrapper(IItemHandler handler) {
//        this.handler = handler;
//    }
//
//    @Override
//    public int getSlots() {
//        return this.handler.getSlots();
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        return this.handler.getStackInSlot(slot);
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//        return this.handler.insertItem(slot, stack, simulate);
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        return this.handler.extractItem(slot, amount, simulate);
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return this.handler.getSlotLimit(slot);
//    }
//}
