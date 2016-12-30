package net.ndrei.teslacorelib.gui;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

/**
 * Created by CF on 2016-12-30.
 */
public class TiledRenderedGuiPiece extends BasicContainerGuiPiece {
    private ResourceLocation texture;
    private int textureX, textureY;
    private int tileWidth, tileHeight;
    private int horizontalTiles, verticalTiles;
    private EnumDyeColor tint;

    public TiledRenderedGuiPiece(int left, int top, int tileWidth, int tileHeight,
                                 int horizontalTiles, int verticalTiles,
                                 ResourceLocation texture, int textureX, int textureY,
                                 EnumDyeColor tint) {
        super(left, top, tileWidth * horizontalTiles, tileHeight * verticalTiles);

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.horizontalTiles = horizontalTiles;
        this.verticalTiles = verticalTiles;

        this.tint = tint;

        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        if (this.texture != null) {
            container.mc.getTextureManager().bindTexture(this.texture);

            for (int x = 0; x < this.horizontalTiles; x++) {
                for (int y = 0; y < this.verticalTiles; y++) {
                    container.drawTexturedModalRect(guiX + this.getLeft() + x * this.tileWidth, guiY + this.getTop() + y * this.tileHeight,
                            this.textureX, this.textureY, this.tileWidth, this.tileHeight);
                }
            }
        }
        if (this.tint != null) {
            container.drawFilledRect(guiX + this.getLeft(), guiY + this.getTop(), this.getWidth(), this.getHeight(),
                    0x24000000 + this.tint.getMapColor().colorValue);
        }
    }
}