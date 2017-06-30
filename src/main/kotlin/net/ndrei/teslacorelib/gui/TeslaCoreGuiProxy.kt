package net.ndrei.teslacorelib.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities

/**
 * Created by CF on 2017-06-28.
 */
class TeslaCoreGuiProxy : IGuiHandler {
    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)
        if (te != null && te.hasCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)) {
            val provider = te.getCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)
            if (provider != null) {
                return provider.getContainer(id, player)
            }
        }
        return null
    }

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)
        if (te != null && te.hasCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)) {
            val provider = te.getCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)
            if (provider != null) {
                return provider.getGuiContainer(id, player)
            }
        }
        return null
    }
}
