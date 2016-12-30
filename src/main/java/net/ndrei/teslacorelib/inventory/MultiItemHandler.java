package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2016-12-16.
 */
public class MultiItemHandler implements IMultiItemHandler {
    private List<IItemHandler> handlers = null;

    public MultiItemHandler(List<IItemHandler> handlers) {
        if (null == (this.handlers = handlers)) {
            this.handlers = Lists.newArrayList();
        }
    }

    public void addItemHandler(IItemHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack) {
        if (slot < 0) {
            return false;
        }
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            if (handler.getSlots() > slot) {
                if (handler instanceof IFilteredItemHandler) {
                    // filter the inputs
                    return ((IFilteredItemHandler)handler).canInsertItem(slot, stack);
                }
                return true;
            }
            slot -= handler.getSlots();
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot) {
        if (slot < 0) {
            return false;
        }
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            if (handler.getSlots() > slot) {
                if (handler instanceof IFilteredItemHandler) {
                    // filter the inputs
                    return ((IFilteredItemHandler)handler).canExtractItem(slot);
                }
                return true;
            }
            slot -= handler.getSlots();
        }
        return false;

    }

    @Override
    public int getInventories() {
        return this.handlers.size();
    }

    @Override
    public IItemHandler getInventory(int inventory) {
        return this.handlers.get(inventory);
    }

    @Override
    public IFilteredItemHandler getFilteredInventory(int inventory) {
        IItemHandler handler = this.getInventory(inventory);
        if (handler instanceof IFilteredItemHandler) {
            return (IFilteredItemHandler) handler;
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int inventory, int slot, ItemStack stack) {
        IFilteredItemHandler handler = this.getFilteredInventory(inventory);
        if (handler != null) {
            return handler.canInsertItem(slot, stack);
        }
        return true;
    }

    @Override
    public boolean canExtractItem(int inventory, int slot) {
        IFilteredItemHandler handler = this.getFilteredInventory(inventory);
        if (handler != null) {
            return handler.canExtractItem(slot);
        }
        return true;
    }

    @Override
    public int getSlots() {
        int slots = 0;
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            slots += handler.getSlots();
        }
        return slots;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0) {
            return ItemStackUtil.getEmptyStack();
        }
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            if (handler.getSlots() > slot) {
                return handler.getStackInSlot(slot);
            }
            slot -= handler.getSlots();
        }
        return ItemStackUtil.getEmptyStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot < 0) {
            return ItemStackUtil.getEmptyStack();
        }
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            if (handler.getSlots() > slot) {
                return handler.insertItem(slot, stack, simulate);
            }
            slot -= handler.getSlots();
        }
        return ItemStackUtil.getEmptyStack();
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot < 0) {
            return ItemStackUtil.getEmptyStack();
        }
        for(int i = 0; i < this.handlers.size(); i++) {
            IItemHandler handler = this.handlers.get(i);
            if (handler.getSlots() > slot) {
                return handler.extractItem(slot, amount, simulate);
            }
            slot -= handler.getSlots();
        }
        return ItemStackUtil.getEmptyStack();
    }

//    @Override
//    public int getSlotLimit(int slot) {
//        if (slot < 0) {
//            return 0;
//        }
//        for(int i = 0; i < this.handlers.size(); i++) {
//            IItemHandler handler = this.handlers.get(i);
//            if (handler.getSlots() > slot) {
//                return handler.getSlotLimit(slot);
//            }
//            slot -= handler.getSlots();
//        }
//        return 0;
//    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot >= 0) {
            for (int i = 0; i < this.handlers.size(); i++) {
                IItemHandler handler = this.handlers.get(i);
                if (handler.getSlots() > slot) {
                    if (handler instanceof IItemHandlerModifiable) {
                        ((IItemHandlerModifiable)handler).setStackInSlot(slot, stack);
                    }
                    else {
                        throw new RuntimeException("Target inventory is not an IItemHandlerModifiable.");
                    }
                    return;
                }
                slot -= handler.getSlots();
            }
        }
    }
}
