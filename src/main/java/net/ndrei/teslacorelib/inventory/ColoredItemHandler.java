package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.items.IItemHandler;

/**
 * Created by CF on 2016-12-17.
 */
public class ColoredItemHandler extends FilteredItemHandler {
    private EnumDyeColor color;
    private String name;

    public ColoredItemHandler(EnumDyeColor color, String name, IItemHandler handler) {
        super(handler);

        this.color = color;
        this.name = name;
    }

    public EnumDyeColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public final ColoredItemHandlerInfo getInfo() {
        return new ColoredItemHandlerInfo(this.getName(), this.getColor());
    }
}
