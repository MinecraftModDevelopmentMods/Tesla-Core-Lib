package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.InitializeDuringConstruction

@Suppress("unused")
@SideOnly(Side.CLIENT)
@InitializeDuringConstruction
object MultiPartBlockEvents {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun drawOutline(ev: DrawBlockHighlightEvent) {
        if (ev.target.subHit == 42) {
            val result = ev.target.hitInfo as? MultiPartRayTraceResult
            if (result != null) {
                result.part.renderOutline(ev)

                if (ev.isCancelable) {
                    ev.isCanceled = true
                }
            }
        }
    }
}
