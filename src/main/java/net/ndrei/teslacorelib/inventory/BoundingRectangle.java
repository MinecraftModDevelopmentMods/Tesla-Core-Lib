package net.ndrei.teslacorelib.inventory;

/**
 * Created by CF on 2016-12-23.
 */
public final class BoundingRectangle {
    private int x, y, width, height;

    public BoundingRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getLeft() { return this.x; }
    public int getTop() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getRight() { return this.getLeft() + this.getWidth(); }
    public int getBottom() { return this.getTop() + this.getHeight(); }
}
