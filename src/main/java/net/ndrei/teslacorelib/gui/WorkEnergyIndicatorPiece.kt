package net.ndrei.teslacorelib.gui

import com.google.common.collect.Lists
import com.mojang.realmsclient.gui.ChatFormatting

/**
 * Created by CF on 2017-06-28.
 */
class WorkEnergyIndicatorPiece(private val provider: IWorkEnergyProvider?, left: Int, top: Int)
    : BasicContainerGuiPiece(left, top, 36, 4) {

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()

        container.drawTexturedModalRect(guiX + this.left, guiY + this.top, 1, 245, this.width, this.height)
        if (this.provider != null) {
            val percent = this.provider.workEnergyStored.toFloat() / this.provider.workEnergyCapacity.toFloat()
            val width = Math.max(0, Math.min(this.width - 2, Math.round((this.width - 2) * percent)))
            if (width > 0) {
                container.drawTexturedModalRect(
                        guiX + this.left + 1, guiY + this.top + 1,
                        2, 251, width, this.height - 2)
            }
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && this.provider != null) {
            val lines = Lists.newArrayList<String>()
            lines.add(String.format("%sWork Energy Buffer", ChatFormatting.DARK_PURPLE))
            lines.add(String.format("%s%,d T %sof %s%,d T",
                    ChatFormatting.AQUA, this.provider.workEnergyStored,
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.RESET, this.provider.workEnergyCapacity))
            lines.add(String.format("%smax: %s+%,d T %s/ tick",
                    ChatFormatting.GRAY,
                    ChatFormatting.AQUA, this.provider.workEnergyTick,
                    ChatFormatting.GRAY))

            val ticks = this.provider.workEnergyCapacity / this.provider.workEnergyTick
            lines.add(String.format("%s~ every %s%,d %sticks",
                    ChatFormatting.GRAY,
                    ChatFormatting.WHITE, ticks,
                    ChatFormatting.GRAY))

            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }
}
