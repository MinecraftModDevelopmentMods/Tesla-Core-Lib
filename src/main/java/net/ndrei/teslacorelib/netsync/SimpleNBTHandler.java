package net.ndrei.teslacorelib.netsync;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by CF on 2016-12-03.
 */
public class SimpleNBTHandler implements IMessageHandler<SimpleNBTMessage, SimpleNBTMessage> {
    @Override
    public SimpleNBTMessage onMessage(SimpleNBTMessage message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            // process client side message
            if ((message != null) && (message.getPos() != null) && (Minecraft.getMinecraft().world != null)) {
                TileEntity entity = Minecraft.getMinecraft().world.getTileEntity(message.getPos());
                if ((entity != null) && (entity instanceof ISimpleNBTMessageHandler)) {
                    return ((ISimpleNBTMessageHandler)entity).handleMessage(message);
                }
            }
        } else {
            // processing server side message
            if (message != null) {
                WorldServer world = DimensionManager.getWorld(message.getDimension());
                if (world != null) {
                    TileEntity entity = world.getTileEntity(message.getPos());
                    if ((entity != null) && (entity instanceof ISimpleNBTMessageHandler)) {
                        return ((ISimpleNBTMessageHandler)entity).handleMessage(message);
                    }
                }
            }
        }
        return null;
    }
}
