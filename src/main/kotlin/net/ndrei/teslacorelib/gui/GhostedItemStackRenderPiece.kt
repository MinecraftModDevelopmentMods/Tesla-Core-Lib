package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.render.GhostedItemRenderer

/**
 * Created by CF on 2017-07-04.
 */
open class GhostedItemStackRenderPiece(left: Int, top: Int, val alpha: Float = .42f, val stack: ItemStack? = null)
    : BasicContainerGuiPiece(left, top, 18, 18) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)

        val left = this.left + guiX + 1
        val top = this.top + guiY + 1

        val stack = this.getRenderStack()
        if (!stack.isEmpty) {
            RenderHelper.enableGUIStandardItemLighting()
            GhostedItemRenderer.renderItemInGUI(container.itemRenderer, stack, left, top, this.alpha)
            RenderHelper.disableStandardItemLighting()
        }
    }

    open fun getRenderStack(): ItemStack {
        return this.stack ?: ItemStack.EMPTY
    }
}
