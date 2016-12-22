package net.ndrei.teslacorelib.gui;

import net.minecraft.util.ResourceLocation;

/**
 * Created by CF on 2016-12-21.
 */
public class BasicRenderedGuiPiece extends BasicContainerGuiPiece {
    private ResourceLocation texture;
    private int textureX, textureY;

    public BasicRenderedGuiPiece(int left, int top, int width, int height,
                                    ResourceLocation texture, int textureX, int textureY) {
        super(left, top, width, height);

        this.texture = texture;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        if (this.texture != null) {
            container.mc.getTextureManager().bindTexture(this.texture);

            container.drawTexturedModalRect(guiX + this.getLeft(), guiY + this.getTop(),
                    this.textureX, this.textureY, this.getWidth(), this.getHeight());
        }
    }
}
