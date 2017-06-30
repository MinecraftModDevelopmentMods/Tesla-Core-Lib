package net.ndrei.teslacorelib.gui

/**
 * Created by CF on 2017-06-28.
 */
class PlayerInventoryBackground(left: Int, top: Int, width: Int, height: Int)
    : BasicContainerGuiPiece(left, top, width, height) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val slots = container.teslaContainer
        if (slots == null || !slots.hasPlayerInventory()) {
            return
        }

        container.bindDefaultTexture()

        var topOffset = 0
        for (i in 0..2) {
            container.drawTexturedModalRect(guiX + this.left, guiY + this.top + topOffset, 7, 159,
                    Math.min(this.width, 162),
                    Math.min(this.height - topOffset, 18))
            topOffset += 18
            if (topOffset > this.height) {
                break
            }
        }
    }
}
