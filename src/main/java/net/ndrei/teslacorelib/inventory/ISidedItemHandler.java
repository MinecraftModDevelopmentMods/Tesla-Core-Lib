package net.ndrei.teslacorelib.inventory;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by CF on 2016-12-14.
 */
public interface ISidedItemHandler extends IItemHandler {
    int[] getSlotsForFace(EnumFacing side);
}
