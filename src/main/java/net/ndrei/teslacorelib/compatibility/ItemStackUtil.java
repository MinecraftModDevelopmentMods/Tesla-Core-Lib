package net.ndrei.teslacorelib.compatibility;

import net.minecraft.item.ItemStack;

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
        return stack.stackSize;
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
}
