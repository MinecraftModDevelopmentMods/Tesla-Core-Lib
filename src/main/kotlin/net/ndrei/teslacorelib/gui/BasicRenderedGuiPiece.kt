package net.ndrei.teslacorelib.gui

import net.minecraft.util.ResourceLocation

/**
 * Created by CF on 2017-06-28.
 */
open class BasicRenderedGuiPiece(left: Int, top: Int, width: Int, height: Int,
                            private val texture: ResourceLocation?, private val textureX: Int, private val textureY: Int)
    : BasicContainerGuiPiece(left, top, width, height) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        if (this.texture != null) {
            container.mc.textureManager.bindTexture(this.texture)

            container.drawTexturedModalRect(guiX + this.left, guiY + this.top,
                    this.textureX, this.textureY, this.width, this.height)
        }
    }
}
