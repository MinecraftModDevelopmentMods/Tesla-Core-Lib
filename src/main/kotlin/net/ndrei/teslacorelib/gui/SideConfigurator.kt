package net.ndrei.teslacorelib.gui

import net.minecraft.util.EnumFacing
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-28.
 */
class SideConfigurator(left: Int, top: Int, width: Int, height: Int, private val sidedConfig: SidedItemHandlerConfig, private val entity: SidedTileEntity)
    : BasicContainerGuiPiece(left, top, width, height) {

    private var selectedInventory = -1
    private var lockPiece: LockedInventoryTogglePiece? = null

    init {
        this.setSelectedInventory(-1)
    }

    fun setSelectedInventory(index: Int) {
        this.selectedInventory = index
        this.lockPiece = null
        this.setVisibility(this.selectedInventory >= 0)

        val colors = this.sidedConfig.coloredInfo
        if (this.selectedInventory in 0..colors.size - 1) {
            val color = colors[this.selectedInventory].color
            if (this.entity.getInventoryLockState(color) != null) {
                this.lockPiece = LockedInventoryTogglePiece(
                        this.left + 6 * 18 + 2,
                        this.top + 1 * 18 + 2,
                        this.entity, color)
            }
        }
    }

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val colors = this.sidedConfig.coloredInfo
        if (this.selectedInventory < 0 || this.selectedInventory >= colors.size) {
            return
        }

        val color = colors[this.selectedInventory].color
        val sides = this.sidedConfig.getSidesForColor(color)
        container.bindDefaultTexture()
        this.drawSide(container, sides, EnumFacing.UP, 2, 0, mouseX, mouseY)
        this.drawSide(container, sides, EnumFacing.WEST, 1, 1, mouseX, mouseY)
        this.drawSide(container, sides, EnumFacing.SOUTH, 2, 1, mouseX, mouseY)
        this.drawSide(container, sides, EnumFacing.EAST, 3, 1, mouseX, mouseY)
        this.drawSide(container, sides, EnumFacing.DOWN, 2, 2, mouseX, mouseY)
        this.drawSide(container, sides, EnumFacing.NORTH, 3, 2, mouseX, mouseY)

        this.lockPiece?.drawBackgroundLayer(container, guiX, guiY, partialTicks, mouseX, mouseY)
    }

    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        super.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY)

        this.lockPiece?.drawForegroundLayer(container, guiX, guiY, mouseX, mouseY)
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        super.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY)

        this.lockPiece?.drawForegroundTopLayer(container, guiX, guiY, mouseX, mouseY)
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (this.selectedInventory >= 0 && this.isInside(container, mouseX, mouseY)) {
            var localY = mouseY - container.guiTop - this.top
            val row = localY / 18
            if (row in 0..2) {
                var localX = mouseX - container.guiLeft - this.left
                val column = localX / 18
                if (column in 1..3) {
                    localX -= column * 18
                    localY -= row * 18
                    if (localX in 2..15 && localY in 2..15) {
                        var facing: EnumFacing? = null
                        if (row == 0) {
                            if (column == 2) {
                                facing = EnumFacing.UP
                            }
                        } else if (row == 1) {
                            if (column == 1) {
                                facing = EnumFacing.WEST
                            } else if (column == 2) {
                                facing = EnumFacing.SOUTH
                            } else if (column == 3) {
                                facing = EnumFacing.EAST
                            }
                        } else if (row == 2) {
                            if (column == 2) {
                                facing = EnumFacing.DOWN
                            } else if (column == 3) {
                                facing = EnumFacing.NORTH
                            }
                        }

                        if (facing != null) {
                            val color = this.sidedConfig.coloredInfo[this.selectedInventory].color
                            this.sidedConfig.toggleSide(color, facing)

                            val nbt = this.entity.setupSpecialNBTMessage("TOGGLE_SIDE")
                            nbt.setInteger("color", color.metadata)
                            nbt.setInteger("side", facing.index)
                            TeslaCoreLib.network.sendToServer(SimpleNBTMessage(this.entity, nbt))
                        }
                    }
                }
            }

            if ((this.lockPiece != null) && Companion.isInside(container, this.lockPiece!!, mouseX, mouseY)) {
                this.lockPiece?.mouseClicked(container, mouseX, mouseY, mouseButton)
            }
        }
    }

    private fun drawSide(container: BasicTeslaGuiContainer<*>, sides: List<EnumFacing>, side: EnumFacing, column: Int, row: Int, mouseX: Int, mouseY: Int) {
        var mouseX = mouseX
        var mouseY = mouseY
        val x = this.left + column * 18 + 2
        val y = this.top + row * 18 + 2

        container.drawTexturedRect(x, y, 110, 210, 14, 14)
        mouseX -= container.guiLeft
        mouseY -= container.guiTop
        if (mouseX >= x && mouseY >= y && mouseX <= x + 14 && mouseY <= y + 14) {
            container.drawFilledRect(container.guiLeft + x + 1, container.guiTop + y + 1, 12, 12, 0x42FFFFFF)
        }
        container.drawTexturedRect(x, y, if (sides.contains(side)) 182 else 146, 210, 14, 14)
    }
}
