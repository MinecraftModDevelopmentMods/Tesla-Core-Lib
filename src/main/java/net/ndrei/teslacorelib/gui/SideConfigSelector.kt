package net.ndrei.teslacorelib.gui

import com.google.common.collect.Lists
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig

/**
 * Created by CF on 2017-06-28.
 */
class SideConfigSelector(left: Int, top: Int, width: Int, height: Int, private val sidedConfig: SidedItemHandlerConfig, private val configurator: SideConfigurator?)
    : BasicContainerGuiPiece(left, top, width, height) {
    private var selectedInventory = -1

    @SideOnly(Side.CLIENT)
    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        val colors = this.sidedConfig.coloredInfo
                .take(8) // to make sure there is no overflow
        if (colors.isNotEmpty()) {
            container.bindDefaultTexture()
            var i = 0
            colors.forEach {
                container.drawTexturedRect(
                        this.left + 2 + i * 18, this.top + 2,
                        if (i == this.selectedInventory) 128 else 110, 210, 14, 14)
                container.drawFilledRect(
                        guiX + this.left + 4 + i * 18, guiY + this.top + 4,
                        10, 10, 0xFF000000.toInt() + it.color.colorValue)
                i++
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun drawForegroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        val colors = this.sidedConfig.coloredInfo

        if (this.isInside(container, mouseX, mouseY)) {
            val localY = mouseY - guiY - this.top
            if (localY in 2..14) {
                var localX = mouseX - guiX - this.left
                val index = localX / 18
                if (index != this.selectedInventory && index >= 0 && index < colors.size && index < 8 && colors[index].highlight != null) {
                    localX = localX - index * 18
                    if (localX in 2..14) {
                        val box = colors[index].highlight
                        container.drawFilledRect(box.left, box.top, box.width, box.height,
                                0x42000000 + colors[index].color.colorValue,
                                0xFF000000.toInt() + colors[index].color.colorValue)
                    }
                }
            }
        }

        if (this.selectedInventory >= 0 && this.selectedInventory < colors.size) {
            val box = colors[this.selectedInventory].highlight
            container.drawFilledRect(box.left, box.top, box.width, box.height,
                    0x42000000 + colors[this.selectedInventory].color.colorValue,
                    0xFF000000.toInt() + colors[this.selectedInventory].color.colorValue)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (this.isInside(container, mouseX, mouseY)) {
            val localY = mouseY - guiY - this.top
            if (localY in 2..14) {
                var localX = mouseX - guiX - this.left
                val index = localX / 18
                val colors = this.sidedConfig.coloredInfo
                if (index >= 0 && index < colors.size && index < 8) {
                    localX -= index * 18
                    if (localX in 2..14) {
                        val label = colors[index].name
                        if (label.isNotEmpty()) {
                            container.drawTooltip(Lists.newArrayList(label),
                                    this.left + index * 18 + 9,
                                    this.top + this.height / 2)
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        val oldIndex = this.selectedInventory
        this.selectedInventory = -1
        if (this.isInside(container, mouseX, mouseY)) {
            val localY = mouseY - container.guiTop - this.top
            if (localY in 2..14) {
                var localX = mouseX - container.guiLeft - this.left
                val index = localX / 18
                val colors = this.sidedConfig.coloredInfo
                if (index != oldIndex && index >= 0 && index < colors.size && index < 8 && colors[index].highlight != null) {
                    localX -= index * 18
                    if (localX in 2..14) {
                        this.selectedInventory = index
                    }
                }
            }
        }

        if (this.configurator != null) {
            this.configurator.setSelectedInventory(this.selectedInventory)
        }

        val slots = container.teslaContainer
        if (slots != null) {
            if (this.selectedInventory >= 0) {
                slots.hidePlayerInventory()
            } else {
                slots.showPlayerInventory()
            }
        }
    }
}
