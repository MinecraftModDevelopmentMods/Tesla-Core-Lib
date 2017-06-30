package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.ndrei.teslacorelib.compatibility.FontRendererUtil

/**
 * Created by CF on 2017-06-28.
 */
class MachineNameGuiPiece(private val unlocalizedName: String?, left: Int, top: Int, width: Int, height: Int)
    : BasicContainerGuiPiece(left, top, width, height) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        if ((this.unlocalizedName != null) && this.unlocalizedName.isNotEmpty()) {
            val title = I18n.format(this.unlocalizedName)
            FontRendererUtil.fontRenderer.drawString(title, guiX + this.left, guiY + this.top, 4210751)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        }
    }
}
