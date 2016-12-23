package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.EnumDyeColor;

/**
 * Created by CF on 2016-12-17.
 */
public final class ColoredItemHandlerInfo {
    private String name;
    private EnumDyeColor color;
    private BoundingRectangle highlight;

    public ColoredItemHandlerInfo(String name, EnumDyeColor color) {
        this.name = name;
        this.color = color;
    }

    public ColoredItemHandlerInfo(String name, EnumDyeColor color, BoundingRectangle highlight) {
        this(name, color);
        this.highlight = highlight;
    }

    public EnumDyeColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public BoundingRectangle getHighlight() { return this.highlight; }
}
