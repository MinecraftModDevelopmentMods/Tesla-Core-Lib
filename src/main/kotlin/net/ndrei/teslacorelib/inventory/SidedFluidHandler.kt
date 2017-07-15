package net.ndrei.teslacorelib.inventory

import com.google.common.collect.Lists
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider

/**
 * Created by CF on 2017-06-28.
 */
class SidedFluidHandler(private val sidedConfig: ISidedItemHandlerConfig)
    : FluidStorage(), ICapabilityProvider, IGuiContainerPiecesProvider, Iterable<IFluidTank> {

    private fun getTanksForSide(facing: EnumFacing?): List<IFluidTank> {
        val list = Lists.newArrayList<IFluidTank>()

        for (tank in this.tanks) {
            if (tank is ColoredFluidHandler) {
                val color = tank.color
                if (facing == null || this.sidedConfig.isSideSet(color, facing)) {
                    list.add(tank)
                }
            }
        }

        return list
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            val tanks = this.getTanksForSide(facing)
            return tanks.size > 0
        }

        return false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            val tanks = this.getTanksForSide(facing)
            val tempStorage = FluidStorage()

            for (tank in tanks) {
                tempStorage.addTank(tank)
            }

            return tempStorage as T
        }

        return null
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val list = Lists.newArrayList<IGuiContainerPiece>()

        for (tank in this.tanks) {
            if (tank is ColoredFluidHandler) {
                val box = tank.boundingBox
                if (box != null) {
                    list.add(FluidTankPiece(tank, box.left, box.top))
                }
            }
        }

        return list
    }

    override fun iterator() = this.tanks.iterator()
}
