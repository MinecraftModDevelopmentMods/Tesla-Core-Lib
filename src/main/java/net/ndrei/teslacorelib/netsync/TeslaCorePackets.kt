package net.ndrei.teslacorelib.netsync

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by CF on 2017-06-28.
 */
class TeslaCorePackets(channelName: String) : ITeslaCorePackets {
    private val wrapper: SimpleNetworkWrapper = SimpleNetworkWrapper(channelName)

    init {
        this.wrapper.registerMessage(SimpleNBTHandler::class.java, SimpleNBTMessage::class.java, 1, Side.CLIENT)
        this.wrapper.registerMessage(SimpleNBTHandler::class.java, SimpleNBTMessage::class.java, 1, Side.SERVER)
    }

    override fun send(message: IMessage) {
        if (message is SimpleNBTMessage) {
            this.wrapper.sendToAllAround(message, message.targetPoint)
        }
        else {
            this.wrapper.sendToAll(message)
        }
    }

    override fun sendToServer(message: IMessage) {
        this.wrapper.sendToServer(message)
    }
}
