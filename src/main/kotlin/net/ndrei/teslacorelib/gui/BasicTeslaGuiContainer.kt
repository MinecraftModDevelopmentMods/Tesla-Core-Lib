package net.ndrei.teslacorelib.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
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
    private lateinit var pieces: List<IGuiContainerPiece>

    init {
        @Suppress("LeakingThis")
        super.xSize = this.containerWidth // 198

        @Suppress("LeakingThis")
        super.ySize = this.containerHeight // 184

        this.refreshParts()
    }

    protected open val containerWidth get() = 198
    protected open val containerHeight get() = 184

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

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawDefaultBackground()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        super.drawScreen(mouseX, mouseY, partialTicks)
        super.renderHoveredToolTip(mouseX, mouseY)
    }

    private fun drawPieces(callback: (IGuiContainerPiece) -> Unit) {
        this.pieces
            .filter { it.isVisible && (it !is SideDrawerPiece) }
            .forEach { callback(it) }
        var top = 5
        this.pieces
            .filterIsInstance<SideDrawerPiece>()
            .filter { it.isVisible }
            .sortedBy { it.topIndex }
            .forEach { it.updateTop(top); top += 14; callback(it) }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.drawGuiContainerBackground()

        this.drawPieces { it.drawBackgroundLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY) }
        this.drawPieces { it.drawMiddleLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY) }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.drawPieces { it.drawForegroundLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY) }
        this.drawPieces { it.drawForegroundTopLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY) }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        this.pieces
            .filter { it.isVisible && BasicContainerGuiPiece.isInside(this, it, mouseX, mouseY) }
            .forEach { it.mouseClicked(this, mouseX, mouseY, mouseButton) }
    }

    open fun drawGuiContainerBackground() {
        this.bindDefaultTexture()
        this.drawTexturedModalRect(super.guiLeft, super.guiTop, 0, 0, super.getXSize(), super.getYSize())
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

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
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
