package net.ndrei.teslacorelib.render

import net.minecraft.util.EnumFacing

/**
 * Created by CF on 2017-06-28.
 */
interface IHudInfoProvider {
    fun getHudLines(face: EnumFacing?): List<HudInfoLine>
}