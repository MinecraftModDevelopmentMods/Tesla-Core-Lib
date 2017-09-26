package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.DrawBlockHighlightEvent
import net.ndrei.teslacorelib.utils.withAlpha
import java.awt.Color

interface IBlockPart {
    val hitBoxes: List<IBlockPartHitBox>

    fun canBeHitWith(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer?, stack: ItemStack) =
        true

    val outlineDepthCheck get() =
        true

    fun getOutlineColor(world: World, pos: BlockPos, state: IBlockState) =
        Color.BLACK.withAlpha(.4f).rgb

    fun getHoverOutlineColor(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, stack: ItemStack)=
        Color.BLUE.withAlpha(.75f).rgb

    fun renderOutline(event: DrawBlockHighlightEvent)
}
