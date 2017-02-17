package net.ndrei.teslacorelib.gui;

import net.ndrei.teslacorelib.inventory.BoundingRectangle;

/**
 * Created by CF on 2017-02-16.
 */
public abstract class ButtonPiece extends BasicContainerGuiPiece {
    public ButtonPiece(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    protected abstract void renderState(BasicTeslaGuiContainer container, boolean over, BoundingRectangle box);
    protected abstract void clicked();

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        this.renderState(container, super.isInside(container, mouseX, mouseY),
                new BoundingRectangle(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight()));
    }

    @Override
    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
        if (BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)) {
            this.clicked();
        }
    }
}