package net.ndrei.teslacorelib.netsync

import net.minecraft.client.Minecraft
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Created by CF on 2017-06-28.
 */
class SimpleNBTHandler : IMessageHandler<SimpleNBTMessage, SimpleNBTMessage> {
    override fun onMessage(message: SimpleNBTMessage?, ctx: MessageContext): SimpleNBTMessage? {
        if (ctx.side.isClient) {
            // process client side message
            if ((message != null) && (message.pos != null) && (Minecraft.getMinecraft().world != null)) {
                val entity = Minecraft.getMinecraft().world.getTileEntity(message.pos)
                if (entity != null && entity is ISimpleNBTMessageHandler) {
                    return (entity as ISimpleNBTMessageHandler).handleServerMessage(message)
                }
            }
        } else {
            // processing server side message
            if ((message != null) && (message.dimension != null) && (message.pos != null)) {
                val world = DimensionManager.getWorld(message.dimension!!)
                if (world != null) {
                    val entity = world.getTileEntity(message.pos!!)
                    if ((entity != null) && (entity is ISimpleNBTMessageHandler)) {
                        return (entity as ISimpleNBTMessageHandler).handleClientMessage(ctx.serverHandler.player, message)
                    }
                }
            }
        }
        return null
    }
}