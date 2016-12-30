package net.ndrei.teslacorelib.compatibility;

import net.minecraft.item.ItemStack;

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
        return stack.getCount();
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
}
