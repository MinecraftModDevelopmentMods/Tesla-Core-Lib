package net.ndrei.teslacorelib.gui

/**
 * Created by CF on 2017-07-05.
 */
abstract class SideDrawerPiece(private val topIndex: Int) : ToggleButtonPiece(-12, 5 + topIndex * 14, 14, 14) {
    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()

        container.drawTexturedModalRect(guiX + this.left, guiY + this.top,
                200,
                if (this.isInside(container, mouseX, mouseY)) 211 else 197,
                this.width, this.height)
    }

    companion object {
        fun findFreeSpot(pieces: List<IGuiContainerPiece>): Int
            = (pieces.map { (it as? SideDrawerPiece)?.topIndex ?: -1 }.max() ?: -1) + 1
    }
}