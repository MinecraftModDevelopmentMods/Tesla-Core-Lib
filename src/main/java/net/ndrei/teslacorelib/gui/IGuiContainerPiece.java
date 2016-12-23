package net.ndrei.teslacorelib.gui;

/**
 * Created by CF on 2016-12-21.
 */
public interface IGuiContainerPiece {
    int getLeft();

    int getTop();

    int getWidth();

    int getHeight();

    void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton);

//    void mouseClickMove(BasicTeslaGuiContainer container, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);

//    void mouseReleased(BasicTeslaGuiContainer container, int mouseX, int mouseY, int state);

    void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY);

    void drawMiddleLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY);

    void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY);

    void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY);

    boolean isVisible();
    void setVisibility(boolean isVisible);
}

