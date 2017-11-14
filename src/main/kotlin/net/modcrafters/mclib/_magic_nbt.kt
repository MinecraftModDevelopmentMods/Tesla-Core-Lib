package net.modcrafters.mclib

import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants

fun NBTTagCompound.setNonNullTag(key: String, tag: NBTBase?) = this.also {
    if (tag != null)
        this.setTag(key, tag)
}

fun NBTTagCompound.checkEmpty() = if (this.size == 0) null else this

fun NBTTagCompound.getNullOrCompound(key: String): NBTTagCompound? =
    if (this.hasKey(key, Constants.NBT.TAG_COMPOUND)) this.getCompoundTag(key) else null

fun NBTBase.wrapInTag(key: String) = NBTTagCompound().also { it.setTag(key, this) }