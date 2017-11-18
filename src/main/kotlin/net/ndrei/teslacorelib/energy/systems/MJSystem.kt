package net.ndrei.teslacorelib.energy.systems

import buildcraft.api.mj.IMjConnector
import buildcraft.api.mj.IMjReceiver
import buildcraft.api.mj.MjAPI
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.ndrei.teslacorelib.energy.IEnergySystem

object MJSystem : IEnergySystem {
    const val MODID = "buildcraftlib"

    override val ModId get() = MJSystem.MODID

    override fun hasCapability(capability: Capability<*>)
            = this.isAvailable() && ((capability == MjAPI.CAP_RECEIVER) || (capability == MjAPI.CAP_CONNECTOR))

    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T? {
        if (this.isAvailable() && (capability == MjAPI.CAP_RECEIVER)) {
            return MjAPI.CAP_RECEIVER.cast(Wrapper(energy))
        }
        else if (this.isAvailable() && (capability == MjAPI.CAP_CONNECTOR)) {
            return MjAPI.CAP_CONNECTOR.cast(IMjConnector { true })
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