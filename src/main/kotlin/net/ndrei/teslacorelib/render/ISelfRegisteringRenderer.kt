package net.ndrei.teslacorelib.render

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ISelfRegisteringRenderer {
    @SideOnly(Side.CLIENT)
    fun registerRenderer()
}