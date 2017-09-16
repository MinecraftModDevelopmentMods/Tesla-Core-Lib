package net.ndrei.teslacorelib.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.localization.GUI_ENERGY
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslacorelib.localization.makeTextComponent

enum class EnergyDisplayType(val energySystem: String, val lightColor: TextFormatting, val darkColor: TextFormatting, val tesla: Float, val u: Int, val v: Int) {
    TESLA("Tesla", TextFormatting.AQUA, TextFormatting.DARK_AQUA, 1.0f, 21, 192),
    RF("Redstone Flux", TextFormatting.RED, TextFormatting.DARK_RED, 1.0f, 199, 134),
    MJ("BuildCraft MJ", TextFormatting.AQUA, TextFormatting.DARK_AQUA, 10.0f, 227, 134);

    fun fromTesla(tesla: Long) =
        (tesla.toFloat() / this.tesla).toLong()

    fun makeTextComponent(format: TextFormatting? = null) =
        localizeModString(MOD_ID, GUI_ENERGY, this.energySystem) {
            if (format != null) {
                +format
            }
        }

    fun formatPower(power: Long) =
        String.format("%,d", Math.round(power.toDouble() / this.tesla.toDouble()))

    fun makeTextComponent(power: Long, format: TextFormatting? = null) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            if (format != null) {
                +format
            }
            +formatPower(power).makeTextComponent(format)
        }

    fun makeLightTextComponent(power: Long) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            +this@EnergyDisplayType.lightColor
            +formatPower(power).makeTextComponent(this@EnergyDisplayType.lightColor)
        }

    fun makeDarkTextComponent(power: Long) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            +this@EnergyDisplayType.darkColor
            +formatPower(power).makeTextComponent(this@EnergyDisplayType.darkColor)
        }
}
