package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.ndrei.teslacorelib.utils.withAlpha
import java.awt.Color

@Suppress("unused", "MemberVisibilityCanPrivate")
abstract class BlockPart(
    protected val outlineColor: Int = Color.BLACK.withAlpha(.42f).rgb,
    protected val hoverValidOutlineColor: Int = Color.BLUE.withAlpha(.75f).rgb,
    protected val hoverInvalidOutlineColor: Int = Color.RED.withAlpha(.75f).rgb,
    override val outlineDepthCheck: Boolean = true
): IBlockPart {
    protected val boxes = mutableListOf<IBlockPartHitBox>()

    override val hitBoxes: List<IBlockPartHitBox>
        get() = this.boxes.toList()

    override fun getOutlineColor(world: World, pos: BlockPos, state: IBlockState) = this.outlineColor

    override fun getHoverOutlineColor(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, stack: ItemStack)=
        if (this.isItemValid(world, pos, state, player, stack)) { this.hoverValidOutlineColor } else { this.hoverInvalidOutlineColor }

    protected open fun isItemValid(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, stack: ItemStack) =
        true

    override fun renderOutline(event: DrawBlockHighlightEvent) {
        OutlineRenderUtil.renderDefaultOutline(event, this)
    }
}
