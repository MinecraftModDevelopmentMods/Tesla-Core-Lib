package net.ndrei.teslacorelib.capabilities.inventory;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo;

import java.util.List;

/**
 * Created by CF on 2016-12-17.
 */
public interface ISidedItemHandlerConfig {
    List<ColoredItemHandlerInfo> getColoredInfo();
    void addColoredInfo(String name, EnumDyeColor color);
    void addColoredInfo(ColoredItemHandlerInfo info);

    List<EnumFacing> getSidesForColor(EnumDyeColor color);
    void setSidesForColor(EnumDyeColor color, List<EnumFacing> sides);

    boolean isSideSet(EnumDyeColor color, EnumFacing side);
}
