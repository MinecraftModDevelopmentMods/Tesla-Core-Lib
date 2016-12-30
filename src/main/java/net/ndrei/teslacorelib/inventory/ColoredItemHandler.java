package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider;

import java.util.List;

/**
 * Created by CF on 2016-12-17.
 */
public class ColoredItemHandler extends FilteredItemHandler implements IContainerSlotsProvider, IGuiContainerPiecesProvider {
    private EnumDyeColor color;
    private String name;
    private BoundingRectangle boundingBox;
    private IFilteredItemHandler containerItemHandler = null;

    public ColoredItemHandler(IItemHandler handler, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        super(handler);

        this.color = color;
        this.name = name;
        this.boundingBox = boundingBox;
    }

    public EnumDyeColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public BoundingRectangle getBoundingBox() { return this.boundingBox; }

//    public final ColoredItemHandlerInfo getInfo() {
//        return new ColoredItemHandlerInfo(this.getName(), this.getColor());
//    }

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
                    return ColoredItemHandler.this.canInsertItem(slot, stack);
                }
            };
        }
        return this.containerItemHandler;
    }
}
