package net.ndrei.teslacorelib.gui

import com.google.common.collect.Lists
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.resources.I18n
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslacorelib.utils.canFillFrom
import net.ndrei.teslacorelib.utils.getContainedFluid
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-06-28.
 */
class FluidTankPiece(private val tile: SidedTileEntity, private val color: EnumDyeColor, private val tank: IFluidTank, left: Int, top: Int)
    : BasicContainerGuiPiece(left, top, WIDTH, HEIGHT) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left, this.top, 1, 189, this.width, this.height)

        container.drawTexturedRect(this.left + 2, this.top + 2, 48, 191, this.width - 4, this.height - 4)
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
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left + 2, this.top + 2, 63, 191, this.width - 4, this.height - 4)
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY)) {
            val fluid = this.tank.fluid
            val lines = Lists.newArrayList<String>()
            lines.add(String.format("%sFluid: %s%s", ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, fluid?.localizedName ?: "${ChatFormatting.GRAY}Empty"))
            lines.add(String.format("%s%,d mb %sof", ChatFormatting.AQUA, this.tank.fluidAmount, ChatFormatting.DARK_GRAY))
            lines.add(String.format("%s%,d mb", ChatFormatting.RESET, this.tank.capacity))

            val stack = container.mc.player.inventory.itemStack
            if (!stack.isEmpty) {
                val bucket = stack.getContainedFluid()
                lines.add("${ChatFormatting.BLUE}hovering with: ${bucket?.localizedName ?: "${ChatFormatting.DARK_GRAY}${I18n.format(stack.item.getUnlocalizedName(stack))}"}")
                if (bucket != null) {
                    if (this.tank.canFillFrom(stack)) {
                        lines.add("${ChatFormatting.GREEN}accepting fluid")
                    } else {
                        lines.add("${ChatFormatting.RED}not accepting fluid")
                    }
                }
                if (stack.canFillFrom(this.tank)) {
                    lines.add("${ChatFormatting.GREEN}can fill from tank")
                } else {
                    lines.add("${ChatFormatting.RED}can't fill from tank")
                }
            }

            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(container, mouseX, mouseY, mouseButton)

        if (super.isInside(container, mouseX, mouseY)) {
            val stack = container.mc.player.inventory.itemStack
            if (!stack.isEmpty) {
                val bucket = stack.getContainedFluid()
                val canFill = (bucket != null) && this.tank.canFillFrom(stack)
                val canDrain = stack.canFillFrom(this.tank)

                if (canFill || canDrain) {
                    val action = if ((mouseButton == 0) && canFill) "FILL_TANK"
                    else if ((mouseButton == 1) && canDrain) "DRAIN_TANK"
                    else return

                    val nbt = this.tile.setupSpecialNBTMessage(action)
                    nbt.setTag("stack", stack.writeToNBT(NBTTagCompound()))
                    nbt.setInteger("color", this.color.metadata)
                    this.tile.sendToServer(nbt)
                }
            }
        }
    }

    companion object {
        val WIDTH = 18
        val HEIGHT = 54
    }
}
