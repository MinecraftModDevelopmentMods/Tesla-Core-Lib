package net.ndrei.teslacorelib.energy.systems

import buildcraft.api.mj.IMjConnector
import buildcraft.api.mj.IMjReceiver
import buildcraft.api.mj.MjAPI
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import net.ndrei.teslacorelib.energy.IEnergySystem
import net.ndrei.teslacorelib.energy.IGenericEnergyStorage

object MJSystem : IEnergySystem {
    const val MODID = "buildcraftlib"

    override val ModId get() = MJSystem.MODID

    override fun hasCapability(capability: Capability<*>)
            = this.isAvailable() && (capability == MjAPI.CAP_RECEIVER)

    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T? {
        if (this.isAvailable() && (capability == MjAPI.CAP_RECEIVER)) {
            return MjAPI.CAP_RECEIVER.cast(Wrapper(energy))
        }
        return null
    }

    override fun wrapTileEntity(te: TileEntity, side: EnumFacing) = null
    override fun wrapItemStack(stack: ItemStack) = null

    @Optional.Interface(iface = "buildcraft.api.mj.IMjReceiver", modid = MJSystem.MODID, striprefs = true)
    class Wrapper(val energy: IGenericEnergyStorage): IMjReceiver {
        override fun canConnect(other: IMjConnector) = true // I guess ?!?!

        private fun Long.fromJoules(): Long = this / 10 / MjAPI.MJ
        private fun Long.toJoules(): Long = this * 10 * MjAPI.MJ

        override fun getPowerRequested()
                = (this.energy.capacity - this.energy.stored).toJoules()

        override fun receivePower(microJoules: Long, simulate: Boolean)
                = this.energy.givePower(microJoules.fromJoules(), simulate).toJoules()
    }
}