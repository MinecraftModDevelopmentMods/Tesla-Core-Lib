package net.ndrei.teslacorelib.netsync

/**
 * Created by CF on 2017-06-28.
 */
interface ISimpleNBTMessageHandler {
    fun handleMessage(message: SimpleNBTMessage): SimpleNBTMessage?
}