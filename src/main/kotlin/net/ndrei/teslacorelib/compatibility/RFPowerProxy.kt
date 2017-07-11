package net.ndrei.teslacorelib.compatibility

import cofh.redstoneflux.api.IEnergyReceiver
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.Optional

/**
 * Created by CF on 2017-07-10.
 */
object RFPowerProxy {
    const val MODID = "redstoneflux"
    var isRFAvailable: Boolean = false

    @Optional.Method(modid = RFPowerProxy.MODID)
    fun isRFAcceptor(te: TileEntity, facing: EnumFacing)
            = (te is IEnergyReceiver) && te.canConnectEnergy(facing)

    @Optional.Method(modid = RFPowerProxy.MODID)
    fun givePowerTo(te: TileEntity, facing: EnumFacing, power: Long): Long {
        val receiver = (te as? IEnergyReceiver) ?: return 0
        return if (receiver.canConnectEnergy(facing))
            receiver.receiveEnergy(facing, power.toInt(), false).toLong()
        else
            0
    }
}
