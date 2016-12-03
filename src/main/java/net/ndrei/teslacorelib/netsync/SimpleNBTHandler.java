package net.ndrei.teslacorelib.netsync;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.ndrei.teslacorelib.TeslaCoreLib;

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
                    TeslaCoreLib.logger.info("processing message for '" + entity.toString() + "':: " + message.getCompound().toString());
                    return ((ISimpleNBTMessageHandler)entity).handleMessage(message);
                }
            }
        } else {
            // TODO: process server side message
        }
        return null;
    }
}
