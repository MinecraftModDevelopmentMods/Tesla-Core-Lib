package net.ndrei.teslacorelib.gui

import net.minecraft.item.EnumDyeColor
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by CF on 2017-06-28.
 */
class TiledRenderedGuiPiece(left: Int, top: Int, private val tileWidth: Int, private val tileHeight: Int,
                            private val horizontalTiles: Int, private val verticalTiles: Int,
                            private val texture: ResourceLocation?, private val textureX: Int, private val textureY: Int,
                            private val tint: EnumDyeColor?)
    : BasicContainerGuiPiece(left, top, tileWidth * horizontalTiles, tileHeight * verticalTiles) {

    @SideOnly(Side.CLIENT)
    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        if (this.texture != null) {
            container.mc.textureManager.bindTexture(this.texture)

            for (x in 0..this.horizontalTiles - 1) {
                for (y in 0..this.verticalTiles - 1) {
                    container.drawTexturedModalRect(guiX + this.left + x * this.tileWidth, guiY + this.top + y * this.tileHeight,
                            this.textureX, this.textureY, this.tileWidth, this.tileHeight)
                }
            }
        }
        if (this.tint != null) {
            container.drawFilledRect(guiX + this.left, guiY + this.top, this.width, this.height,
                    0x24000000 + this.tint.colorValue)
        }
    }
}
