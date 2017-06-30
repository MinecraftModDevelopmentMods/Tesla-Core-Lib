package net.ndrei.teslacorelib.netsync

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.ndrei.teslacorelib.TeslaCoreLib

/**
 * Created by CF on 2017-06-28.
 */
class SimpleNBTMessage(pos: BlockPos?, dimension: Int?, compound: NBTTagCompound?) : IMessage {
    var compound: NBTTagCompound? = null
        private set
    var pos: BlockPos? = null
        private set
    var dimension: Int? = null
        private set

    @Suppress("unused")
    constructor() : this(null, null, null)

    constructor(entity: TileEntity, compound: NBTTagCompound)
            : this(entity.pos, entity.world.provider.dimension, compound)

    init {
        this.pos = pos
        this.dimension = dimension
        this.compound = compound
    }

    override fun fromBytes(buf: ByteBuf) {
        val pos = ByteBufUtils.readTag(buf)
        if (pos != null) {
            this.pos = BlockPos(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z"))
            this.dimension = pos.getInteger("dim")
        } else {
            TeslaCoreLib.logger.warn("Network package received with missing BlockPos information.")
            this.pos = BlockPos(0, 0, 0)
            this.dimension = 0
        }
        this.compound = ByteBufUtils.readTag(buf) ?: NBTTagCompound()
    }

    override fun toBytes(buf: ByteBuf) {
        val pos = NBTTagCompound()
        if (this.pos != null) {
            pos.setInteger("x", this.pos!!.x)
            pos.setInteger("y", this.pos!!.y)
            pos.setInteger("z", this.pos!!.z)
        }
        if (this.dimension != null) {
            pos.setInteger("dim", this.dimension!!)
        }
        ByteBufUtils.writeTag(buf, pos)
        if (this.compound != null) {
            ByteBufUtils.writeTag(buf, this.compound)
        }
    }

    val targetPoint: NetworkRegistry.TargetPoint?
        get() = if ((this.dimension != null) && (this.pos != null))
            NetworkRegistry.TargetPoint(this.dimension!!, this.pos!!.x.toDouble(), this.pos!!.y.toDouble(), this.pos!!.z.toDouble(), 64.0)
        else null
}
