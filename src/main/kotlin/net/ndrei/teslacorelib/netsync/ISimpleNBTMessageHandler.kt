package net.ndrei.teslacorelib.netsync

import net.minecraft.entity.player.EntityPlayerMP

/**
 * Created by CF on 2017-06-28.
 */
interface ISimpleNBTMessageHandler {
    fun handleClientMessage(player: EntityPlayerMP?, message: SimpleNBTMessage): SimpleNBTMessage?
    fun handleServerMessage(message: SimpleNBTMessage) : SimpleNBTMessage?
}