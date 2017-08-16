package net.ndrei.teslacorelib.test

import net.minecraft.item.EnumDyeColor
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredFluidHandler
import net.ndrei.teslacorelib.inventory.FluidTank
import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-06-27.
 */
class TeslaCoreFluidsTestEntity : ElectricMachine(-1) {
    private lateinit var waterTank: IFluidTank
    private lateinit var lavaTank: IFluidTank
    private lateinit var tempTank: IFluidTank

    private lateinit var outputTank: IFluidTank

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        // auto filtered
        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.lavaTank = object: FluidTank(5000) {
            override fun onContentsChanged() {
                this@TeslaCoreFluidsTestEntity.markDirty()
            }

            override fun canFillFluidType(fluid: FluidStack?)
                    = fluid?.fluid == FluidRegistry.LAVA
        }
        super.addFluidTank(this.lavaTank, EnumDyeColor.RED, "Lava Tank",
                BoundingRectangle(43 + 18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.tempTank = object: FluidTank(5000) {
            override fun onContentsChanged() {
                this@TeslaCoreFluidsTestEntity.markDirty()
            }
        }
        super.addFluidTank(object: ColoredFluidHandler(this.tempTank, EnumDyeColor.LIME, "Temp Tank",
                BoundingRectangle(43 + 18 + 18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            // override fun acceptsFluid(fluid: FluidStack) = fluid.fluid == FluidRegistry.WATER
        }, null)
        super.ensureFluidItems()

        this.outputTank = super.addSimpleFluidTank(5000, "Output Tank", EnumDyeColor.WHITE,
                43 + 18 + 18 + 18 + 18, 25, true)
    }

    //#endregion

    override fun performWork(): Float {
        arrayOf(this.waterTank, this.lavaTank, this.tempTank).forEach {
            val drained = it.drain(100, false)
            if ((drained != null) && (drained.amount > 0)) {
                val filled = this.outputTank.fill(drained, false)
                if (filled > 0) {
                    this.outputTank.fill(it.drain(filled, true), true)
                    return 1.0f
                }
            }
        }

        return 0.0f
    }
}
