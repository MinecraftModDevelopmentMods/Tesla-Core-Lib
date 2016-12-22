package net.ndrei.teslacorelib.containers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import net.ndrei.teslacorelib.inventory.IFilteredItemHandler;

/**
 * Created by CF on 2016-12-22.
 */
public class FilteredSlot extends SlotItemHandler {
    private IFilteredItemHandler handler;

    public FilteredSlot(IFilteredItemHandler handler, int index, int xPosition, int yPosition) {
        super(handler, index, xPosition, yPosition);

        this.handler = handler;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.handler.canInsertItem(this.getSlotIndex(), stack);
    }
}
