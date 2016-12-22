package net.ndrei.teslacorelib.gui;

import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo;

import java.util.List;

/**
 * Created by CF on 2016-12-22.
 */
public class SideConfigurator extends BasicContainerGuiPiece {
    private SidedItemHandlerConfig sidedConfig;

    public SideConfigurator(int left, int top, int width, int height, SidedItemHandlerConfig sidedConfig) {
        super(left, top, width, height);

        this.sidedConfig = sidedConfig;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();
        if ((colors != null) && (colors.size() > 0)) {
            container.bindDefaultTexture();
            for (int i = 0; (i < colors.size()) && (i < 9); i++) {
                container.drawTexturedRect(
                        this.getLeft() + 2 + i * 18, this.getTop() + 2,
                        110, 210, 14, 14);
                container.drawFilledRect(
                        guiX + this.getLeft() + 4 + i * 18, guiY + this.getTop() + 4,
                        10, 10, 0xFF000000 + colors.get(i).getColor().getMapColor().colorValue);
            }
        }
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
    }

    @Override
    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
        BasicTeslaContainer slots = container.getTeslaContainer();
        if (slots != null) {
            if (slots.hasPlayerInventory()) {
                slots.hidePlayerInventory();
            }
            else {
                slots.showPlayerInventory();
            }
        }
    }
}
