package net.ndrei.teslacorelib.netsync

import net.minecraftforge.fml.common.network.simpleimpl.IMessage

/**
 * Created by CF on 2017-06-28.
 */
interface ITeslaCorePackets {
    fun send(message: IMessage)
    fun sendToServer(message: IMessage)
}