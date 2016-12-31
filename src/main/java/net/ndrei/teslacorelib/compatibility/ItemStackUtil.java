package net.ndrei.teslacorelib.compatibility;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * Created by CF on 2016-12-30.
 */
public final class ItemStackUtil {
    public static boolean isEmpty(ItemStack stack) {
        return (stack == null) || (stack.stackSize == 0);
    }

    public static void grow(ItemStack stack, int amount) {
        stack.stackSize += amount;
    }

    public static void shrink(ItemStack stack, int amount) {
        stack.stackSize -= amount;
    }

    public static int getSize(ItemStack stack) {
        return ItemStackUtil.isEmpty(stack) ? 0 : stack.stackSize;
    }

    public static void setSize(ItemStack stack, int size) {
        stack.stackSize = size;
    }

    public static ItemStack copyWithSize(ItemStack stack, int size) {
        ItemStack result = stack.copy();
        result.stackSize = size;
        return result;
    }

    public static ItemStack getEmptyStack() {
        return null;
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
                match.stackSize = (match.stackSize + stack.stackSize);
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
            if ((temp == null) || (temp.getItem() != stack.getItem())) {
                continue;
            }

            ItemStack takenStack = handler.extractItem(i, Math.min(amount, temp.stackSize), false);
            taken += takenStack.stackSize;
            amount -= takenStack.stackSize;
            if (amount <= 0) {
                break;
            }
        }
        return taken;
    }
}
