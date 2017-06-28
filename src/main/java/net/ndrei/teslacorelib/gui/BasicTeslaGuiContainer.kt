package net.ndrei.teslacorelib.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.RenderItem
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.FontRendererUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import java.io.IOException

/**
 * Created by CF on 2017-06-28.
 */
open class BasicTeslaGuiContainer<out T : SidedTileEntity>(val guiId: Int, container: Container, val entity: T) : GuiContainer(container) {
    private var pieces: List<IGuiContainerPiece>? = null

    init {
        super.xSize = 198
        super.ySize = 184
        this.refreshParts()
    }

    val teslaContainer: BasicTeslaContainer<*>?
        get() {
            if (this.inventorySlots is BasicTeslaContainer<*>) {
                return this.inventorySlots as BasicTeslaContainer<*>
            }
            return null
        }

    fun bindDefaultTexture() {
        BasicTeslaGuiContainer.bindDefaultTexture(this)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.bindDefaultTexture()
        this.drawTexturedModalRect(super.guiLeft, super.guiTop, 0, 0, super.getXSize(), super.getYSize())

        for (piece in this.pieces!!) {
            if (!piece.isVisible) {
                continue
            }

            piece.drawBackgroundLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY)
        }

        for (piece in this.pieces!!) {
            if (!piece.isVisible) {
                continue
            }

            piece.drawMiddleLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        for (piece in this.pieces!!) {
            if (!piece.isVisible) {
                continue
            }

            piece.drawForegroundLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY)
        }

        for (piece in this.pieces!!) {
            if (!piece.isVisible) {
                continue
            }

            piece.drawForegroundTopLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY)
        }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        for (piece in this.pieces!!) {
            if (piece.isVisible && BasicContainerGuiPiece.isInside(this, piece, mouseX, mouseY)) {
                piece.mouseClicked(this, mouseX, mouseY, mouseButton)
            }
        }
    }

    fun drawTexturedRect(x: Int, y: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
        super.drawTexturedModalRect(super.guiLeft + x, super.guiTop + y, textureX, textureY, width, height)
    }

    fun drawTooltip(textLines: List<String>, x: Int, y: Int) {
        super.drawHoveringText(textLines, x, y)
    }

    fun drawFilledRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        super.drawGradientRect(x, y, x + width, y + height, color, color)
    }

    fun drawFilledRect(x: Int, y: Int, width: Int, height: Int, color: Int, strokeColor: Int) {
        this.drawFilledRect(x, y, width, height, color)

        super.drawHorizontalLine(x, x + width - 1, y, strokeColor)
        super.drawVerticalLine(x, y, y + height - 1, strokeColor)
        super.drawVerticalLine(x + width - 1, y, y + height - 1, strokeColor)
        super.drawHorizontalLine(x, x + width - 1, y + height - 1, strokeColor)
    }

    val itemRenderer: RenderItem
        get() = super.itemRender

    // TODO: not sure if this is different than super.fontRenderer... find out!
    val fontRenderer: FontRenderer
        get() = FontRendererUtil.fontRenderer

    fun setZIndex(zLevel: Float) {
        this.zLevel = zLevel
    }

    private fun refreshParts() {
        this.pieces = this.entity.getGuiContainerPieces(this)
    }

    companion object {
        val MACHINE_BACKGROUND = ResourceLocation(TeslaCoreLib.MODID, "textures/gui/basic-machine.png")

        fun bindDefaultTexture(container: GuiContainer) {
            container.mc.textureManager.bindTexture(MACHINE_BACKGROUND)
        }

        fun refreshParts(world: World?) {
            if (world != null && world.isRemote && Minecraft.getMinecraft().currentScreen is BasicTeslaGuiContainer<*>) {
                (Minecraft.getMinecraft().currentScreen as BasicTeslaGuiContainer<*>).refreshParts()
            }
        }
    }
}
