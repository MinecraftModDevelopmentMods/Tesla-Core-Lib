package net.ndrei.teslacorelib.capabilities.container;

import net.minecraft.entity.player.EntityPlayer;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider;

/**
 * Created by CF on 2016-12-21.
 */
public interface IGuiContainerProvider extends IContainerSlotsProvider, IGuiContainerPiecesProvider {
    BasicTeslaContainer getContainer(int id, EntityPlayer player);
    BasicTeslaGuiContainer getGuiContainer(int id, EntityPlayer player);
}
