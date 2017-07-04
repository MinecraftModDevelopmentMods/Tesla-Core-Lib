package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack

/**
 * Created by CF on 2017-07-04.
 */
open class ItemStackRenderPiece(left: Int, top: Int, val stack: ItemStack? = null): BasicContainerGuiPiece(left, top, 18, 18) {

    override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawMiddleLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)

        val left = this.left + guiX + 1
        val top = this.top + guiY + 1

        val stack = this.getRenderStack()
        if (!stack.isEmpty) {
            RenderHelper.enableGUIStandardItemLighting()
            container.itemRenderer.renderItemAndEffectIntoGUI(stack, left, top)
            RenderHelper.disableStandardItemLighting()

            container.itemRenderer.renderItemOverlayIntoGUI(container.fontRenderer, stack, left, top, "0")
        }
    }

    open fun getRenderStack(): ItemStack {
        return this.stack ?: ItemStack.EMPTY
    }
}
