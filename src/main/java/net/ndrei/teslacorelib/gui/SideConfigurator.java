package net.ndrei.teslacorelib.gui;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;

import java.util.List;

/**
 * Created by CF on 2016-12-23.
 */
public class SideConfigurator extends BasicContainerGuiPiece {
    private SidedItemHandlerConfig sidedConfig;
    private ElectricTileEntity entity;

    private int selectedInventory = -1;

    public SideConfigurator(int left, int top, int width, int height, SidedItemHandlerConfig sidedConfig, ElectricTileEntity entity) {
        super(left, top, width, height);

        this.sidedConfig = sidedConfig;
        this.entity = entity;
        this.setSelectedInventory(-1);
    }

    public void setSelectedInventory(int index) {
        this.setVisibility((this.selectedInventory = index) >= 0);
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();
        if ((this.selectedInventory < 0) || (this.selectedInventory >= colors.size())) {
            return;
        }

        EnumDyeColor color = colors.get(this.selectedInventory).getColor();
        List<EnumFacing> sides = this.sidedConfig.getSidesForColor(color);
        container.bindDefaultTexture();
        this.drawSide(container, sides, EnumFacing.UP, 2, 0, mouseX, mouseY);
        this.drawSide(container, sides, EnumFacing.WEST, 1, 1, mouseX, mouseY);
        this.drawSide(container, sides, EnumFacing.SOUTH, 2, 1, mouseX, mouseY);
        this.drawSide(container, sides, EnumFacing.EAST, 3, 1, mouseX, mouseY);
        this.drawSide(container, sides, EnumFacing.DOWN, 2, 2, mouseX, mouseY);
        this.drawSide(container, sides, EnumFacing.NORTH, 3, 2, mouseX, mouseY);
    }

    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
        if ((this.selectedInventory >= 0) && this.isInside(container, mouseX, mouseY)) {
            int localY = mouseY - container.getGuiTop() - this.getTop();
            int row = localY / 18;
            if ((row >= 0) && (row <= 2)) {
                int localX = mouseX - container.getGuiLeft() - this.getLeft();
                int column = localX / 18;
                if ((column >= 1) && (column <= 3)) {
                    localX = localX - column * 18;
                    localY = localY - row * 18;
                    if ((localX >= 2) && (localX < 16) && (localY >= 2) && (localY < 16)) {
                        EnumFacing facing = null;
                        if (row == 0) {
                            if (column == 2) {
                                facing = EnumFacing.UP;
                            }
                        } else if (row == 1) {
                            if (column == 1) {
                                facing = EnumFacing.WEST;
                            } else if (column == 2) {
                                facing = EnumFacing.SOUTH;
                            } else if (column == 3) {
                                facing = EnumFacing.EAST;
                            }
                        } else if (row == 2) {
                            if (column == 2) {
                                facing = EnumFacing.DOWN;
                            } else if (column == 3) {
                                facing = EnumFacing.NORTH;
                            }
                        }

                        if (facing != null) {
                            EnumDyeColor color = this.sidedConfig.getColoredInfo().get(this.selectedInventory).getColor();
                            this.sidedConfig.toggleSide(color, facing);

                            NBTTagCompound nbt = this.entity.setupSpecialNBTMessage("TOGGLE_SIDE");
                            nbt.setInteger("color", color.getMetadata());
                            nbt.setInteger("side", facing.getIndex());
                            TeslaCoreLib.network.sendToServer(new SimpleNBTMessage(this.entity, nbt));
                        }
                    }
                }
            }
        }
    }

    private void drawSide(BasicTeslaGuiContainer container, List<EnumFacing> sides, EnumFacing side, int column, int row, int mouseX, int mouseY) {
        int x = this.getLeft() + column * 18 + 2;
        int y = this.getTop() + row * 18 + 2;

        container.drawTexturedRect(x, y, 110, 210, 14, 14);
        mouseX -= container.getGuiLeft();
        mouseY -= container.getGuiTop();
        if ((mouseX >= x) && (mouseY >= y) && (mouseX <= x + 14) && (mouseY <= y + 14)) {
            container.drawFilledRect(container.getGuiLeft() + x + 1, container.getGuiTop() + y + 1, 12, 12, 0x42FFFFFF);
        }
        container.drawTexturedRect(x, y, sides.contains(side) ? 182 : 146, 210, 14, 14);
    }
}
