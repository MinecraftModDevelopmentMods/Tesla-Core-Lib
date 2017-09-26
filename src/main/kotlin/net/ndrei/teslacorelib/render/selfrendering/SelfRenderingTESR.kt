package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.ndrei.teslacorelib.blocks.AxisAlignedBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.render.selfrendering.ISelfRenderingBlock
import net.ndrei.teslacorelib.render.selfrendering.TESRProxy

object SelfRenderingTESR : TileEntitySpecialRenderer<TileEntity>() {
    private val proxy = object: TESRProxy {
        override val rendererDispatcher: TileEntityRendererDispatcher?
            get() = this@SelfRenderingTESR.rendererDispatcher
        override val world: World
            get() = this@SelfRenderingTESR.world
        override val fontRenderer: FontRenderer
            get() = this@SelfRenderingTESR.fontRenderer

        override fun setLightmapDisabled(disabled: Boolean) {
            this@SelfRenderingTESR.setLightmapDisabled(disabled)
        }

        override fun bindTexture(location: ResourceLocation) {
            this@SelfRenderingTESR.bindTexture(location)
        }
    }

    override fun render(te: TileEntity?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (te != null) {
            val block = te.blockType
            if (block is ISelfRenderingBlock) {
                Minecraft.getMinecraft().mcProfiler.startSection("SelfRenderingTESR")
                try {
                    val state = te.world?.getBlockState(te.pos)
                    val facing = if ((state != null) && (state.block is OrientedBlock<*>)) {
                        state.getValue(AxisAlignedBlock.FACING)
                    } else null

                    GlStateManager.pushMatrix()

                    GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
                    when (facing) {
                        null -> {
                            GlStateManager.rotate(120f, 0.0f, 1.0f, 0.0f)
                            GlStateManager.rotate(30f, 0.0f, 0.0f, 1.0f)
                        }
                        EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
                        EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
                        EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
                        else -> {
                        }
                    }
                    GlStateManager.translate(-0.5, 0.0, -0.5)

                    val magicNumber = 0.03125f
                    GlStateManager.scale(magicNumber, -magicNumber, magicNumber)

                    block.renderTESR(this.proxy, te, x, y, z, partialTicks, destroyStage, alpha)

                    GlStateManager.popMatrix()
                } finally {
                    Minecraft.getMinecraft().mcProfiler.endSection()
                }
            }
        }

        super.render(te, x, y, z, partialTicks, destroyStage, alpha)
    }
}