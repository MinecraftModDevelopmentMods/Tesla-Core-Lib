package net.ndrei.teslacorelib.capabilities.container

import net.minecraft.entity.player.EntityPlayer
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider

/**
 * Created by CF on 2017-06-28.
 */
interface IGuiContainerProvider : IContainerSlotsProvider, IGuiContainerPiecesProvider {
    fun getContainer(id: Int, player: EntityPlayer): BasicTeslaContainer<*>?
    fun getGuiContainer(id: Int, player: EntityPlayer): BasicTeslaGuiContainer<*>?
}
