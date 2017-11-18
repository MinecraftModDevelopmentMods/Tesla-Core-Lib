package net.ndrei.teslacorelib.energy.systems

import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.ndrei.teslacorelib.energy.IEnergySystem

object ForgeEnergySystem : IEnergySystem {
    const val MODID = "minecraft"

    override val ModId: String get() = MODID

    override fun hasCapability(capability: Capability<*>)
            = (capability == CapabilityEnergy.ENERGY)

    @Suppress("UNCHECKED_CAST")
    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T?
            = if (capability == CapabilityEnergy.ENERGY) (Wrapper(energy) as? T) else null

    class Wrapper(val energy: IGenericEnergyStorage) : IEnergyStorage {
        override fun canExtract() = this.energy.canTake
        override fun getMaxEnergyStored() = this.energy.capacity.toInt()
        override fun getEnergyStored() = this.energy.stored.toInt()
        override fun canReceive() = this.energy.canGive

        override fun extractEnergy(maxExtract: Int, simulate: Boolean)
                = this.energy.takePower(maxExtract.toLong(), simulate).toInt()

        override fun receiveEnergy(maxReceive: Int, simulate: Boolean)
                = this.energy.givePower(maxReceive.toLong(), simulate).toInt()
    }

    override fun wrapTileEntity(te: TileEntity, side: EnumFacing): IGenericEnergyStorage? {
        if (te is IEnergyStorage) {
            // maybe someone didn't understand capabilities too well
            return ReverseWrapper(te)
        }

        if (te.hasCapability(CapabilityEnergy.ENERGY, side)) {
            val energy = te.getCapability(CapabilityEnergy.ENERGY, side)
            if (energy != null) {
               return ReverseWrapper(energy)
            }
        }

        return null
    }

    override fun wrapItemStack(stack: ItemStack): IGenericEnergyStorage? {
        if (!stack.isEmpty && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            val energy = stack.getCapability(CapabilityEnergy.ENERGY, null)
            if (energy != null) {
                return ReverseWrapper(energy)
            }
        }
        return null
    }

    class ReverseWrapper(val energy: IEnergyStorage) : IGenericEnergyStorage {
        override val capacity get() = this.energy.maxEnergyStored.toLong()
        override val stored get() = this.energy.energyStored.toLong()
        override val canGive get() = this.energy.canReceive()
        override fun givePower(power: Long, simulated: Boolean)
                = this.energy.receiveEnergy(power.toInt(), simulated).toLong()

        override val canTake get() = this.energy.canExtract()
        override fun takePower(power: Long, simulated: Boolean)
                = this.energy.extractEnergy(power.toInt(), simulated).toLong()
    }
}
