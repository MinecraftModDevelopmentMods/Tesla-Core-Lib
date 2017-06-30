package net.ndrei.teslacorelib.gui

import com.google.common.collect.Lists
import com.mojang.realmsclient.gui.ChatFormatting
import net.ndrei.teslacorelib.inventory.EnergyStorage

/**
 * Created by CF on 2017-06-28.
 */
class TeslaEnergyLevelPiece(left: Int, top: Int, private val energyStorage: EnergyStorage?) : BasicContainerGuiPiece(left, top, 18, 54) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        BasicTeslaGuiContainer.bindDefaultTexture(container)

        container.drawTexturedRect(this.left, this.top, 1, 189, this.width, this.height)

        if (this.energyStorage != null) {
            val power = this.energyStorage.energyStored * (this.height - 6) / this.energyStorage.maxEnergyStored

            container.drawTexturedRect(this.left + 2, this.top + 2, 20, 191, this.width - 4, this.height - 4)
            container.drawTexturedRect(this.left + 3, this.top + 3 + this.height - 6 - power, 35, 192 + this.height - 6 - power, this.width - 6, power + 2)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && this.energyStorage != null) {
            val lines = Lists.newArrayList<String>()
            lines.add(String.format("%sStored Energy", ChatFormatting.DARK_PURPLE))
            lines.add(String.format("%s%,d T %sof", ChatFormatting.AQUA, this.energyStorage.energyStored, ChatFormatting.DARK_GRAY))
            lines.add(String.format("%s%,d T", ChatFormatting.RESET, this.energyStorage.maxEnergyStored))

            val tick = this.energyStorage.lastTickEnergy
            val average = this.energyStorage.averageEnergyPerTick
            lines.add(String.format("%s%,d T %s(%s%,d T%s)",
                    if (average < 0) ChatFormatting.RED else ChatFormatting.BLUE, average,
                    ChatFormatting.RESET,
                    if (tick < 0) ChatFormatting.RED else ChatFormatting.BLUE, tick,
                    ChatFormatting.RESET
            ))
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }
}
