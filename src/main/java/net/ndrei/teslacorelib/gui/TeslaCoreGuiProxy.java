package net.ndrei.teslacorelib.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.capabilities.container.IGuiContainerProvider;

/**
 * Created by CF on 2016-12-21.
 */
public class TeslaCoreGuiProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if ((te != null) && te.hasCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)) {
            IGuiContainerProvider provider = te.getCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null);
            if (provider != null) {
                return provider.getContainer(id, player);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if ((te != null) && te.hasCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)) {
            IGuiContainerProvider provider = te.getCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null);
            if (provider != null) {
                return provider.getGuiContainer(id, player);
            }
        }
        return null;
    }
}
