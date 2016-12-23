package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Created by CF on 2016-12-15.
 */
public interface IFilteredItemHandler extends IItemHandlerModifiable {
    boolean canInsertItem(int slot, ItemStack stack);
    boolean canExtractItem(int slot);
}

