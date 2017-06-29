package net.ndrei.teslacorelib.netsync;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings("unused")
public class TeslaCorePackets implements ITeslaCorePackets {
    private SimpleNetworkWrapper wrapper;

    public TeslaCorePackets(String channelName) {
        this.wrapper = new SimpleNetworkWrapper(channelName);

        this.wrapper.registerMessage(SimpleNBTHandler.class, SimpleNBTMessage.class, 1, Side.CLIENT);
        this.wrapper.registerMessage(SimpleNBTHandler.class, SimpleNBTMessage.class, 1, Side.SERVER);
    }

    @Override
    public void send(IMessage message) {
        NetworkRegistry.TargetPoint target = null;
        if (message instanceof SimpleNBTMessage) {
            target = ((SimpleNBTMessage)message).getTargetPoint();
        }

        if (target == null) {
            this.wrapper.sendToAll(message);
        }
        else {
            this.wrapper.sendToAllAround(message, target);
        }
    }

    @Override
    public void sendToServer(IMessage message) {
        this.wrapper.sendToServer(message);
    }
}

