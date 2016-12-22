package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider;

import java.awt.*;
import java.util.List;

/**
 * Created by CF on 2016-12-21.
 */
public class ColoredContainedItemInventory extends ColoredItemHandler implements IContainerSlotsProvider, IGuiContainerPiecesProvider {
    private IFilteredItemHandler containerItemHandler = null;

    public ColoredContainedItemInventory(EnumDyeColor color, String name, IItemHandler handler) {
        super(color, name, handler);
    }

    @Override
    public List<Slot> getSlots(BasicTeslaContainer container) {
        return Lists.newArrayList();
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        return Lists.newArrayList();
    }

    protected IFilteredItemHandler getItemHandlerForContainer() {
        if (this.containerItemHandler == null) {
            this.containerItemHandler = new FilteredItemHandler(this.handler) {
                @Override
                public boolean canInsertItem(int slot, ItemStack stack) {
                    return ColoredContainedItemInventory.this.canInsertItem(slot, stack);
                }
            };
        }
        return this.containerItemHandler;
    }
}
