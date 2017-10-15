package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.GlStateManager

interface IGuiIcon {
    val texture: IGuiTexture
    val left: Int
    val top: Int
    val width: Int
    val height: Int

    fun drawCentered(container: BasicTeslaGuiContainer<*>, piece: BasicContainerGuiPiece, offset: Boolean) {
        this.drawCentered(container, piece,
            if (offset) -container.guiLeft else 0,
            if (offset) -container.guiTop else 0)
    }

    fun drawCentered(container: BasicTeslaGuiContainer<*>, piece: BasicContainerGuiPiece, offsetX: Int = 0, offsetY: Int = 0) {
        this.drawCentered(container, piece.left + offsetX, piece.top + offsetY, piece.width, piece.height)
    }

    fun drawCentered(container: BasicTeslaGuiContainer<*>, left: Int, top: Int, width: Int, height: Int) {
        this.texture.bind(container)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1f, 1f, 1f, 1f)

        container.drawTexturedRect(left + (width - this.width) / 2, top + (height - this.height) / 2,
            this.left, this.top,
            this.width, this.height)

        GlStateManager.disableBlend()
    }

    fun getStaticPiece(left: Int, top: Int) =
        BasicRenderedGuiPiece(left, top, this.width, this.height, this.texture.resource, this.left, this.top)

    fun addStaticPiece(list: MutableList<IGuiContainerPiece>, left: Int, top: Int) =
        list.add(this.getStaticPiece(left, top))
}
