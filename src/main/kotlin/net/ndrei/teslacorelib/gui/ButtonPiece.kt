package net.ndrei.teslacorelib.gui

import net.ndrei.teslacorelib.inventory.BoundingRectangle

/**
 * Created by CF on 2017-06-28.
 */
@Suppress("unused")
abstract class ButtonPiece(left: Int, top: Int, width: Int, height: Int) : BasicContainerGuiPiece(left, top, width, height) {

    protected abstract fun renderState(container: BasicTeslaGuiContainer<*>, over: Boolean, box: BoundingRectangle)
    protected abstract fun clicked()

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        this.renderState(container, super.isInside(container, mouseX, mouseY),
                BoundingRectangle(this.left, this.top, this.width, this.height))
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)) {
            this.clicked()
        }
    }
}
