package net.ndrei.teslacorelib.compatibility;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2016-12-30.
 */
public final class ItemStackUtil {
    public static boolean isEmpty(ItemStack stack) {
        return (stack == null) || stack.isEmpty() || (stack.getCount() == 0);
    }

    public static void grow(ItemStack stack, int amount) {
        stack.grow(amount);
    }

    public static void shrink(ItemStack stack, int amount) {
        stack.shrink(amount);
    }

    public static int getSize(ItemStack stack) {
        return ItemStackUtil.isEmpty(stack) ? 0 : stack.getCount();
    }

    public static void setSize(ItemStack stack, int size) {
        stack.setCount(size);
    }

    public static ItemStack copyWithSize(ItemStack stack, int size) {
        ItemStack result = stack.copy();
        result.setCount(size);
        return result;
    }

    public static ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> getCombinedInventory(IItemHandler handler) {
        List<ItemStack> list = Lists.newArrayList();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (ItemStackUtil.isEmpty(stack)) {
                continue;
            }

            ItemStack match = null;
            for (ItemStack existing : list) {
                if (existing.getItem() == stack.getItem()) {
                    match = existing;
                    break;
                }
            }
            if (match == null) {
                list.add(stack.copy());
            } else {
                match.setCount(match.getCount() + stack.getCount());
            }
        }
        return list;
    }

    public static int extractFromCombinedInventory(IItemHandler handler, ItemStack stack, int amount) {
        if (ItemStackUtil.isEmpty(stack)) {
            return 0;
        }

        int taken = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack temp = handler.getStackInSlot(i);
            if ((temp == null) || temp.isEmpty() || (temp.getItem() != stack.getItem())) {
                continue;
            }

            ItemStack takenStack = handler.extractItem(i, Math.min(amount, temp.getCount()), false);
            taken += takenStack.getCount();
            amount -= takenStack.getCount();
            if (amount <= 0) {
                break;
            }
        }
        return taken;
    }

    @Nonnull
    public static ItemStack insertItemInExistingStacks(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate)
    {
        if (dest == null || stack.isEmpty())
            return ItemStack.EMPTY;

        for (int i = 0; i < dest.getSlots(); i++)
        {
            if (ItemStackUtil.isEmpty(dest.getStackInSlot(i))) {
                continue;
            }

            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }
}
