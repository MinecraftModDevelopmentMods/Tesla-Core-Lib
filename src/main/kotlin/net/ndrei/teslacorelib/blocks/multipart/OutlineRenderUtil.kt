package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import java.awt.Color

object OutlineRenderUtil {
    fun renderDefaultOutline(event: DrawBlockHighlightEvent, part: IBlockPart) =
        this.renderOutline(event, { offset ->
            val aabb = part.hitBoxes.fold<IBlockPartHitBox, AxisAlignedBB?>(null) { ab, hb ->
                if (ab == null) hb.aabb
                else ab.union(hb.aabb)
            } ?: return@renderOutline false

            val stack = when (event.player!!.activeHand ?: EnumHand.MAIN_HAND) {
                EnumHand.MAIN_HAND -> event.player.heldItemMainhand
                EnumHand.OFF_HAND -> event.player.heldItemOffhand
            }
            val targetState = event.player.world.getBlockState(event.target.blockPos)
            val color = Color(if (stack.isEmpty)
                part.getOutlineColor(event.player.world, event.target.blockPos, targetState)
            else part.getHoverOutlineColor(event.player.world, event.target.blockPos, targetState, event.player, stack)
                , true)

            RenderGlobal.drawSelectionBoundingBox(aabb.grow(1.0 / 32.0).offset(offset),
                color.red.toFloat() / 255f,
                color.green.toFloat() / 255f,
                color.blue.toFloat() / 255f,
                color.alpha.toFloat() / 255f)
            return@renderOutline true
        }, false, part)

    fun renderOutline(event: DrawBlockHighlightEvent,
                      renderCallback: (offset: Vec3d) -> Boolean,
                      translateOffset: Boolean = false, part: IBlockPart): Boolean {
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
        GlStateManager.glLineWidth(2.0f)
        GlStateManager.disableTexture2D()
        if (!part.outlineDepthCheck) GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        val blockPos = event.target.blockPos
        val blockState = event.player.world.getBlockState(blockPos)

        var rendered = false
        if ((blockState.material !== Material.AIR) && event.player.world.worldBorder.contains(blockPos)) {
            val dx = event.player.lastTickPosX + (event.player.posX - event.player.lastTickPosX) * event.partialTicks.toDouble()
            val dy = event.player.lastTickPosY + (event.player.posY - event.player.lastTickPosY) * event.partialTicks.toDouble()
            val dz = event.player.lastTickPosZ + (event.player.posZ - event.player.lastTickPosZ) * event.partialTicks.toDouble()

            val offset = Vec3d(dx, dy, dz).subtractReverse(Vec3d(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()))

            if (translateOffset) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(offset.x, offset.y, offset.z)
            }
            rendered = renderCallback(offset)
            if (translateOffset) {
                GlStateManager.popMatrix()
            }
        }

        if (!part.outlineDepthCheck) GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        return rendered
    }
}