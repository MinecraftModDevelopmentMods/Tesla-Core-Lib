package net.ndrei.teslacorelib.netsync;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by CF on 2016-12-03.
 */
public interface ITeslaCorePackets {
    void send(IMessage message);
}
