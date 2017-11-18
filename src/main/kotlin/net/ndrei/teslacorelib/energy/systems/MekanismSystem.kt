package net.ndrei.teslacorelib.energy.systems

import mekanism.api.energy.IStrictEnergyAcceptor
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.ndrei.teslacorelib.energy.IEnergySystem

object MekanismSystem : IEnergySystem {
    const val MODID = "mekanism"

    override val ModId get() = MODID

    override fun hasCapability(capability: Capability<*>)
            = this.isAvailable() && (capability.name == "mekanism.api.energy.IStrictEnergyAcceptor")

    @Suppress("UNCHECKED_CAST")
    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T? {
        return if (hasCapability(capability)) Wrapper(energy) as? T else null
    }

    override fun wrapTileEntity(te: TileEntity, side: EnumFacing): IGenericEnergyStorage? = null
    override fun wrapItemStack(stack: ItemStack): IGenericEnergyStorage? = null

    @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = MODID)
    class Wrapper(val energy: IGenericEnergyStorage): IStrictEnergyAcceptor {
        override fun canReceiveEnergy(side: EnumFacing?) = this.energy.canGive

        // math from : http://wiki.aidancbrady.com/wiki/Energy_Unit_Conversion
        // might be wrong... I suck at math!
        private fun Long.toMekanismUnit() = this.toDouble() * 2.5
        private fun Double.fromMekanismUnit() = (this * 0.4).toLong()

        override fun acceptEnergy(side: EnumFacing?, amount: Double, simulate: Boolean)
                = this.energy.givePower(if (amount == 1.0) 1 else amount.fromMekanismUnit(), simulate)
                .let{if ((it == 1L) && (amount == 1.0)) 1.0 else it.toMekanismUnit() }
    }
}
