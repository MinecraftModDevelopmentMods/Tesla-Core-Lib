package net.ndrei.teslacorelib.inventory;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Created by CF on 2016-12-29.
 */
public class ColoredFluidHandler extends FilteredFluidTank {
    private EnumDyeColor color;
    private String name;
    private BoundingRectangle boundingBox;

    public ColoredFluidHandler(IFluidTank tank, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        super(tank);

        this.color = color;
        this.name = name;
        this.boundingBox = boundingBox;
    }

    public ColoredFluidHandler(Fluid filter, IFluidTank tank, EnumDyeColor color, String name, BoundingRectangle boundingBox) {
        super(filter, tank);

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
}
