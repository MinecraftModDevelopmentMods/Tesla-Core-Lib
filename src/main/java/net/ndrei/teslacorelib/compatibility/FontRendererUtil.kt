package net.ndrei.teslacorelib.compatibility

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by CF on 2017-06-28.
 */
@SideOnly(Side.CLIENT)
object FontRendererUtil {
    val fontRenderer: FontRenderer
        get() = Minecraft.getMinecraft().fontRenderer
}