package net.ndrei.teslacorelib.gui

/**
 * Created by CF on 2017-06-28.
 */
abstract class BasicContainerGuiPiece protected constructor(override val left: Int, top: Int, override val width: Int, override val height: Int) : IGuiContainerPiece {
    private var visible = true
    override var top: Int = top
        protected set

    override val isVisible get() = this.visible

    override fun setVisibility(isVisible: Boolean) {
        this.visible = isVisible
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {}

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {}

    override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {}

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {}

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {}

    protected fun isInside(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int): Boolean {
        return BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)
    }

    companion object {
        fun isInside(container: BasicTeslaGuiContainer<*>, piece: IGuiContainerPiece, mouseX: Int, mouseY: Int): Boolean {
            val l = container.guiLeft + piece.left
            if (mouseX < l || mouseX > l + piece.width) {
                return false
            }

            val t = container.guiTop + piece.top
            return mouseY >= t && mouseY <= t + piece.height
        }
    }
}
