package net.ndrei.teslacorelib.gui

import com.google.common.collect.Lists
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraftforge.fluids.IFluidTank
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-28.
 */
class FluidTankPiece(private val tank: IFluidTank?, left: Int, top: Int) : BasicContainerGuiPiece(left, top, WIDTH, HEIGHT) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left, this.top, 1, 189, this.width, this.height)

        container.drawTexturedRect(this.left + 2, this.top + 2, 48, 191, this.width - 4, this.height - 4)
        if (this.tank != null) {
            val stack = this.tank.fluid
            if (stack != null && stack.amount > 0) {
                val amount = stack.amount * (this.height - 6) / tank.capacity
                if (stack.fluid != null) {
                    val fluid = stack.fluid
                    val color = fluid.getColor(stack)
                    val still = fluid.getFlowing(stack) //.getStill(stack);
                    if (still != null) {
                        var sprite = container.mc.textureMapBlocks.getTextureExtry(still.toString())
                        if (sprite == null) {
                            sprite = container.mc.textureMapBlocks.missingSprite
                        }
                        container.mc.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                        GL11.glColor3ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte())
                        GlStateManager.enableBlend()
                        container.drawTexturedModalRect(
                                guiX + this.left + 3,
                                guiY + this.top + 3 + this.height - 6 - amount,
                                sprite!!,
                                this.width - 6, amount)
                        GlStateManager.disableBlend()
                    }
                }
            }
        }
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left + 2, this.top + 2, 63, 191, this.width - 4, this.height - 4)
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && this.tank != null) {
            val fluid = this.tank.fluid
            if (fluid != null) { // && (fluid.amount > 0)) {
                val lines = Lists.newArrayList<String>()
                lines.add(String.format("%sFluid: %s%s", ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, fluid.localizedName))
                lines.add(String.format("%s%,d mb %sof", ChatFormatting.AQUA, this.tank.fluidAmount, ChatFormatting.DARK_GRAY))
                lines.add(String.format("%s%,d mb", ChatFormatting.RESET, this.tank.capacity))
                container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
            }
        }
    }

    companion object {
        val WIDTH = 18
        val HEIGHT = 54
    }
}
