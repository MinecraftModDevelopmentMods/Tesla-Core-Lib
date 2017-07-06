package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.compatibility.FontRendererUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle

/**
 * Created by CF on 2017-06-28.
 */
@Suppress("unused")
abstract class ToggleButtonPiece(left: Int, top: Int, width: Int, height: Int)
    : BasicContainerGuiPiece(left, top, width, height) {

    protected open val currentState: Int = 0
    protected open fun getStateToolTip(state: Int): List<String> = listOf()
    protected abstract fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle)
    protected abstract fun clicked()

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
//        val mx = mouseX - container.guiLeft
//        val my = mouseY - container.guiTop
//        if ((mx in this.left..(this.left + this.width)) && (my in this.top..(this.top + this.height))) {
        if (super.isInside(container, mouseX, mouseY)) {
            container.drawFilledRect(container.guiLeft + this.left, container.guiTop + this.top, this.width, this.height, 0x42FFFFFF)
        }
    }

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        val state = this.currentState
        val x = this.left + (this.width - 16) / 2
        val y = this.top + (this.height - 16) / 2
        this.renderState(container, state, BoundingRectangle(x, y, 16, 16))
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY)) {
            val tt = this.getStateToolTip(this.currentState)
            if (!tt.isEmpty()) {
                container.drawTooltip(tt, this.left + this.width / 2, this.top + this.height / 2)
            }
        }
    }

    protected fun renderItemStack(container: BasicTeslaGuiContainer<*>, stack: ItemStack?, box: BoundingRectangle) {
        if (stack != null) {
            val item = container.itemRenderer

            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            container.itemRenderer.renderItemAndEffectIntoGUI(stack, box.left, box.top)
            container.itemRenderer.renderItemOverlayIntoGUI(FontRendererUtil.fontRenderer, stack, box.left, box.top, null)
            GlStateManager.popMatrix()
            RenderHelper.disableStandardItemLighting()
            item.renderItemOverlayIntoGUI(container.fontRenderer, stack, box.left, box.top, null)
        }
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (super.isInside(container, mouseX, mouseY)) {
            this.clicked()
        }
    }
}
