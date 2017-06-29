package net.ndrei.teslacorelib.netsync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings("unused")
public class SimpleNBTMessage implements IMessage {
    private NBTTagCompound compound = null;
    private BlockPos pos = null;
    private int dimension;

    @SuppressWarnings("unused")
    public SimpleNBTMessage() { }

    public SimpleNBTMessage(TileEntity entity, NBTTagCompound compound) {
        this(entity.getPos(), entity.getWorld().provider.getDimension(), compound);
    }

    public SimpleNBTMessage(BlockPos pos, int dimension, NBTTagCompound compound) {
        this.pos = pos;
        this.dimension = dimension;
        this.compound = compound;
    }

    public BlockPos getPos() { return this.pos; }
    public int getDimension() { return this.dimension; }
    public NBTTagCompound getCompound() { return this.compound; }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound pos = ByteBufUtils.readTag(buf);
        if (pos != null) {
            this.pos = new BlockPos(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z"));
            this.dimension = pos.getInteger("dim");
        }
        this.compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound pos = new NBTTagCompound();
        if (this.pos != null) {
            pos.setInteger("x", this.pos.getX());
            pos.setInteger("y", this.pos.getY());
            pos.setInteger("z", this.pos.getZ());
        }
        pos.setInteger("dim", this.dimension);
        ByteBufUtils.writeTag(buf, pos);
        ByteBufUtils.writeTag(buf, (this.compound != null) ? this.compound : new NBTTagCompound());
    }

    public NetworkRegistry.TargetPoint getTargetPoint() {
        if (this.pos != null) {
            return new NetworkRegistry.TargetPoint(this.dimension, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64);
        }
        return null;
    }
}

