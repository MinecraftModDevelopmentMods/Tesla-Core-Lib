package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by CF on 2016-12-16.
 */
public interface IMultiItemHandler extends IFilteredItemHandler {
    int getInventories();
    IItemHandler getInventory(int inventory);
    IFilteredItemHandler getFilteredInventory(int inventory);

    boolean canInsertItem(int inventory, int slot, ItemStack stack);
    boolean canExtractItem(int inventory, int slot);
}
