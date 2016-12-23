package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by CF on 2016-12-15.
 */
public class SidedItemHandler extends MultiItemHandler implements ISidedItemHandler {
    private SidedItemHandlerConfig sidedConfig = null;

    public SidedItemHandler(SidedItemHandlerConfig config) {
        this(null, config);
    }

    public SidedItemHandler(List<IItemHandler> handlers, SidedItemHandlerConfig config) {
        super(handlers);

        // this.facesConfig = new HashMap<>();
        this.sidedConfig = config;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] result = new int[0];

        int index = 0;
        for(int i = 0; i < this.getInventories(); i++) {
            IItemHandler handler = this.getInventory(i);
            int size = handler.getSlots();
            if (handler instanceof ColoredItemHandler) {
                EnumDyeColor color = ((ColoredItemHandler)handler).getColor();
                if (this.sidedConfig.isSideSet(color, side)) {
                    int startIndex = result.length;
                    result = Arrays.copyOf(result, result.length + size);
                    for(int x = 0; x < size; x++) {
                        result[startIndex + x] = index + x;
                    }
                }
            }
            index += size;
        }

        return result;
    }

    public SidedItemHandlerWrapper getSideWrapper(EnumFacing side) {
        // TODO: cache them!
        return new SidedItemHandlerWrapper(this, side);
    }
}
