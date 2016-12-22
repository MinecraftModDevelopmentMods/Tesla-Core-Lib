package net.ndrei.teslacorelib.gui;

import net.ndrei.teslacorelib.containers.BasicTeslaContainer;

/**
 * Created by CF on 2016-12-22.
 */
public class PlayerInventoryBackground extends BasicContainerGuiPiece {
    public PlayerInventoryBackground(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        BasicTeslaContainer slots = container.getTeslaContainer();
        if ((slots == null) || !slots.hasPlayerInventory()) {
            return;
        }

        container.bindDefaultTexture();

        int topOffset = 0;
        for(int i = 0; i < 3; i++) {
            container.drawTexturedModalRect(guiX + this.getLeft(), guiY + this.getTop() + topOffset, 7, 159,
                    Math.min(this.getWidth(), 162),
                    Math.min(this.getHeight() - topOffset, 18));
            topOffset += 18;
            if (topOffset > this.getHeight()) {
                break;
            }
        }
    }
}
