package net.ndrei.teslacorelib.render

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-07-12.
 */
@SideOnly(Side.CLIENT)
object SidedTileEntityRenderer : TileEntitySpecialRenderer<SidedTileEntity>() {
    override fun render(te: SidedTileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        te.getRenderers().forEach {
            it.setRendererDispatcher(this.rendererDispatcher)

            it.render(te, x, y, z, partialTicks, destroyStage, alpha)
        }
    }

    override fun isGlobalRenderer(te: SidedTileEntity): Boolean {
        return te.getRenderers().any { it.isGlobalRenderer(te) }
    }
}
