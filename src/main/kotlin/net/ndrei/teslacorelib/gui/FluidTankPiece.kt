package net.ndrei.teslacorelib.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fluids.IFluidTank
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslacorelib.inventory.IFluidTankWrapper
import net.ndrei.teslacorelib.inventory.ITypedFluidTank
import net.ndrei.teslacorelib.localization.GUI_FLUID_TANK
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslacorelib.localization.makeTextComponent
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import net.ndrei.teslacorelib.utils.canFillFrom
import net.ndrei.teslacorelib.utils.getContainedFluid

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
                    GlStateManager.color((color shr 16 and 0xFF).toFloat() / 255f,
                        (color shr 8 and 0xFF).toFloat() / 255f,
                        (color and 0xFF).toFloat() / 255f,
                        ((color ushr 24) and 0xFF).toFloat() / 255f)
                    GlStateManager.enableBlend()
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
                    container.drawTexturedModalRect(
                            guiX + this.left + 3,
                            guiY + this.top + 3 + (if (fluid.isGaseous) 0 else this.height - 6 - amount),
                            sprite!!,
                            this.width - 6, amount)
                    GlStateManager.disableBlend()
                }
            }
        }
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        container.bindDefaultTexture()
        container.drawTexturedRect(this.left + 2, this.top + 2, 63, 191, this.width - 4, this.height - 4)
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY)) {
            val fluid = this.tank.fluid

            fun makeFluidAmountComponent(amount: Int, format: TextFormatting? = null) =
                localizeModString(MOD_ID, GUI_FLUID_TANK, "fluid_amount_format") {
                    if (format != null) {
                        +format
                    }
                    +String.format("%,d", amount).makeTextComponent(format)
                }

            val lines = mutableListOf(
                localizeModString(MOD_ID, GUI_FLUID_TANK, "fluid") {
                    +TextFormatting.DARK_PURPLE
                    +localizeModString(fluid?.unlocalizedName, MOD_ID, GUI_FLUID_TANK, "fluid_empty") {
                        +TextFormatting.LIGHT_PURPLE
                    }
                }.formattedText,
                localizeModString(MOD_ID, GUI_FLUID_TANK, "fluid_amount") {
                    +TextFormatting.DARK_GRAY
                    +makeFluidAmountComponent(this@FluidTankPiece.tank.fluidAmount, TextFormatting.AQUA)
                    +makeFluidAmountComponent(this@FluidTankPiece.tank.capacity, TextFormatting.DARK_AQUA)
                }.formattedText
            )

            val stack = container.mc.player.inventory.itemStack
            if (!stack.isEmpty) {
                val bucket = stack.getContainedFluid()
                lines.add(
                    localizeModString(MOD_ID, GUI_FLUID_TANK, "hovering with") {
                        +TextFormatting.BLUE
                        +localizeModString(bucket?.fluid?.unlocalizedName.let {
                            when {
                                it.isNullOrEmpty() -> stack.unlocalizedName.let {
                                    when {
                                        it.endsWith(".name") -> it
                                        else -> it + ".name"
                                    }
                                }
                                else -> it!!
                            }
                        }) { +TextFormatting.GRAY }
                    }.formattedText
                )
                if (bucket != null) {
                    if (this.tank.canFillFrom(stack)) {
                        lines.add(localizeModString(MOD_ID, GUI_FLUID_TANK, "accepting fluid") {
                            +TextFormatting.GREEN
                        }.formattedText)
                    } else {
                        lines.add(localizeModString(MOD_ID, GUI_FLUID_TANK, "not accepting fluid") {
                            +TextFormatting.RED
                        }.formattedText)
                    }
                }
                if (stack.canFillFrom(this.tank.let {
                    if ((it is ITypedFluidTank) && (it is IFluidTankWrapper) && (it.tankType == FluidTankType.INPUT))
                        it.innerTank
                    else
                        it
                })) {
                    lines.add(localizeModString(MOD_ID, GUI_FLUID_TANK, "can fill from container") {
                        +TextFormatting.GREEN
                    }.formattedText)
//                } else {
//                    lines.add("${ChatFormatting.RED}can't fill from tank")
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
                val canDrain = stack.canFillFrom(this.tank.let {
                    if ((it is ITypedFluidTank) && (it is IFluidTankWrapper) && (it.tankType == FluidTankType.INPUT))
                        it.innerTank
                    else
                        it
                })

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
