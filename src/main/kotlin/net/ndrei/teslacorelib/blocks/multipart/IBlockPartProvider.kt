package net.ndrei.teslacorelib.blocks.multipart

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand

interface IBlockPartProvider {
    fun getParts(): List<IBlockPart>
    fun onPartActivated(player: EntityPlayer, hand: EnumHand, part: IBlockPart, hitBox: IBlockPartHitBox): Boolean
}
