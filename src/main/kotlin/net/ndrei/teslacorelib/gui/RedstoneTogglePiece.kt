package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.GlStateManager
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.IRedstoneControlledMachine
import net.ndrei.teslacorelib.localization.GuiPieceType
import net.ndrei.teslacorelib.localization.localizeModString

/**
 * Created by CF on 2017-06-29.
 */
class RedstoneTogglePiece(private val machine: IRedstoneControlledMachine, left: Int = 153, top: Int = 83) : ToggleButtonPiece(left, top, 14, 14) {
    override val currentState: Int
        get() = this.machine.redstoneControl.ordinal

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()
        container.drawTexturedRect( this.left, this.top,
                110, 210, 14, 14)

        if (super.isInside(container, mouseX, mouseY)) {
            container.drawFilledRect(container.guiLeft + this.left + 1, container.guiTop + this.top + 1, this.width - 2, this.height - 2, 0x42FFFFFF)
        }
    }

    override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
        container.bindDefaultTexture()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        container.drawTexturedModalRect(box.left, box.top,
                145 + (state % 3) * 18, 226, 14, 14)
        GlStateManager.disableBlend()
    }

    override fun getStateToolTip(state: Int): List<String> =
        when (state % 3) {
            0 -> listOf(localizeModString(MOD_ID, GuiPieceType.REDSTONE.key, "Always Active").formattedText)
            1 -> listOf(localizeModString(MOD_ID, GuiPieceType.REDSTONE.key, "Active on Redstone Signal").formattedText)
            2 -> listOf(localizeModString(MOD_ID, GuiPieceType.REDSTONE.key, "Active without Redstone Signal").formattedText)
            else -> listOf()
        }

    override fun clicked() = this.machine.toggleRedstoneControl()
}