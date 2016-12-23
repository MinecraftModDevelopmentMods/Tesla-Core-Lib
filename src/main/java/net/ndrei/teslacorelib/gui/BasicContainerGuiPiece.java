package net.ndrei.teslacorelib.gui;

/**
 * Created by CF on 2016-12-21.
 */
public abstract class BasicContainerGuiPiece implements IGuiContainerPiece {
    private int left, top, width, height;
    private boolean isVisible = true;

    protected BasicContainerGuiPiece(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getLeft() {
        return this.left;
    }

    @Override
    public int getTop() {
        return this.top;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
    }

//    @Override
//    public void mouseClickMove(BasicTeslaGuiContainer container, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//    }

//    @Override
//    public void mouseReleased(BasicTeslaGuiContainer container, int mouseX, int mouseY, int state) {
//    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public void drawMiddleLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
    }

    public static boolean isInside(BasicTeslaGuiContainer container, IGuiContainerPiece piece, int mouseX, int mouseY) {
        int l = container.getGuiLeft() + piece.getLeft();
        if ((mouseX < l) || (mouseX > (l + piece.getWidth()))) {
            return false;
        }

        int t = container.getGuiTop() + piece.getTop();
        return ((mouseY >= t) && (mouseY <= (t + piece.getHeight())));
    }

    protected boolean isInside(BasicTeslaGuiContainer container, int mouseX, int mouseY) {
//        int l = container.getGuiLeft() + this.getLeft();
//        if ((mouseX < l) || (mouseX > (l + this.getWidth()))) {
//            return false;
//        }
//
//        int t = container.getGuiTop() + this.getTop();
//        return ((mouseY >= t) && (mouseY <= (t + this.getHeight())));
        return BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY);
    }
}
