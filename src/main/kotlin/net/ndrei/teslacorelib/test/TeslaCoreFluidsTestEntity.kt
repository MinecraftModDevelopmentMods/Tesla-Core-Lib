package net.ndrei.teslacorelib.test

import net.minecraft.item.EnumDyeColor
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.inventory.FluidTankType
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

        this.waterTank = this.addSimpleFluidTank(5000, "Water Tank", EnumDyeColor.BLUE,
                43, 25, FluidTankType.INPUT, { it.fluid === FluidRegistry.WATER })

        this.lavaTank = this.addSimpleFluidTank(5000, "Lava Tank", EnumDyeColor.RED,
                43 + 18, 25, FluidTankType.INPUT, { it.fluid === FluidRegistry.LAVA })

        this.tempTank = this.addSimpleFluidTank(5000, "Temp Tank", EnumDyeColor.LIME,
                43+18+18, 25, FluidTankType.INPUT)
        super.ensureFluidItems()

        this.outputTank = this.addSimpleFluidTank(5000, "Output Tank", EnumDyeColor.WHITE,
                43 + 18 + 18 + 18 + 18, 25, FluidTankType.OUTPUT)
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
