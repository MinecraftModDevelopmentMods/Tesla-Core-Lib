package net.ndrei.teslacorelib.inventory

import net.minecraft.item.EnumDyeColor
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.IFluidTank

/**
 * Created by CF on 2017-06-28.
 */
class ColoredFluidHandler : FilteredFluidTank {
    val color: EnumDyeColor
    val name: String
    val boundingBox: BoundingRectangle

    constructor(tank: IFluidTank, color: EnumDyeColor, name: String, boundingBox: BoundingRectangle)
            : super(tank) {
        this.color = color
        this.name = name
        this.boundingBox = boundingBox
    }

    constructor(filter: Fluid, tank: IFluidTank, color: EnumDyeColor, name: String, boundingBox: BoundingRectangle)
            : super(filter, tank) {
        this.color = color
        this.name = name
        this.boundingBox = boundingBox
    }
}
