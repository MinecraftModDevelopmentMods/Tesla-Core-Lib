@file:Suppress("unused")
package net.ndrei.teslacorelib.utils

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand

fun<E> MutableList<E>.alsoAdd(vararg thing: E) = this.also { it.addAll(thing) }

fun EntityPlayer.getHeldItem(): ItemStack =
    when (this.activeHand) {
        // EnumHand.MAIN_HAND -> this.heldItemMainhand
        EnumHand.OFF_HAND -> this.heldItemOffhand
        else -> this.heldItemMainhand
    }

fun NBTTagCompound.hasPath(path: String) =
    path.split('.')
        .fold<String, NBTBase?>(this, { nbt, item ->
            if ((nbt != null) && (nbt is NBTTagCompound) && nbt.hasKey(item)) {
                nbt.getTag(item)
            } else null
        }) != null
