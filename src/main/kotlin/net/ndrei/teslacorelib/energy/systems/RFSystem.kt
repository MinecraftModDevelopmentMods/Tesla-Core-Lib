package net.ndrei.teslacorelib.energy.systems

import cofh.redstoneflux.api.IEnergyContainerItem
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.ndrei.teslacorelib.compatibility.RFPowerProxy
import net.ndrei.teslacorelib.energy.IEnergySystem

object RFSystem : IEnergySystem {
    override val ModId: String get() = RFPowerProxy.MODID

    override fun hasCapability(capability: Capability<*>) = false
    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T? = null

    override fun wrapTileEntity(te: TileEntity, side: EnumFacing): IGenericEnergyStorage? {
        if (RFPowerProxy.isRFAvailable && RFPowerProxy.isRFAcceptor(te, side)) {
            return ReverseWrapper(te, side)
        }
        return null
    }

    override fun wrapItemStack(stack: ItemStack): IGenericEnergyStorage? {
        if (RFPowerProxy.isRFAvailable && !stack.isEmpty && (stack.item is IEnergyContainerItem)) {
            return ReverseItemWrapper(stack, stack.item as IEnergyContainerItem)
        }
        return null
    }

    class ReverseWrapper(val rf: TileEntity, val side: EnumFacing) : IGenericEnergyStorage {
        override val capacity get() = RFPowerProxy.getMaxEnergyStored(rf, side).toLong()
        override val stored get() = RFPowerProxy.getEnergyStored(rf, side).toLong()
        override val canGive get() = true

        override fun givePower(power: Long, simulated: Boolean)
                = RFPowerProxy.givePowerTo(this.rf, this.side, power, simulated)

        override val canTake get() = false
        override fun takePower(power: Long, simulated: Boolean) = 0L
    }

    class ReverseItemWrapper(val stack: ItemStack, val item: IEnergyContainerItem): IGenericEnergyStorage {
        override val capacity get() = this.item.getMaxEnergyStored(this.stack).toLong()
        override val stored get() = this.item.getEnergyStored(this.stack).toLong()
        override val canGive get() = true // assume?!?
        override val canTake get() = true // assume?!?

        override fun givePower(power: Long, simulated: Boolean)
                = this.item.receiveEnergy(this.stack, power.toInt(), simulated).toLong()


        override fun takePower(power: Long, simulated: Boolean)
                = this.item.extractEnergy(this.stack, power.toInt(), simulated).toLong()
    }
}