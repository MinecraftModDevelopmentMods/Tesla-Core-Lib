package net.ndrei.teslacorelib.capabilities.inventory

import com.google.common.collect.Lists
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo

/**
 * Created by CF on 2017-06-28.
 */
open class SidedItemHandlerConfig : ISidedItemHandlerConfig, INBTSerializable<NBTTagList> {
    private val facesConfig = hashMapOf<EnumDyeColor, MutableList<EnumFacing>>()
    private var information = mutableListOf<ColoredItemHandlerInfo>()

    override fun isSideSet(color: EnumDyeColor, side: EnumFacing): Boolean {
        if (this.facesConfig.containsKey(color)) {
            val list = this.facesConfig[color]
            if ((list != null) && list.contains(side)) {
//                for (facing in list) {
//                    if (facing == side) {
//                        return true
//                    }
//                }
                return true
            }
        }
        return false
    }

    fun toggleSide(color: EnumDyeColor, side: EnumFacing): Boolean {
        if (this.facesConfig.containsKey(color)) {
            val list = this.facesConfig[color]
            if (list != null && list.contains(side)) {
                list.remove(side)
            } else if (list != null) {
                list.add(side)
            } else
            /*if (list == null)*/ {
                this.setSidesForColor(color, Lists.newArrayList(side))
            }
        } else {
            this.setSidesForColor(color, Lists.newArrayList(side))
        }
        this.updated()
        return this.isSideSet(color, side)
    }

    override val coloredInfo: List<ColoredItemHandlerInfo>
        get() = this.information
                .toList()
                .filter { !it.highlight.isEmpty }
                .sortedBy { it.index }

    override fun addColoredInfo(name: String, color: EnumDyeColor, highlight: BoundingRectangle) {
        this.addColoredInfo(name, color, highlight, (this.information.map { it.index }.max() ?: 0) + 10)
    }

    override fun addColoredInfo(name: String, color: EnumDyeColor, highlight: BoundingRectangle, index: Int) {
        this.addColoredInfo(ColoredItemHandlerInfo(name, color, highlight, index))
    }

    override fun addColoredInfo(info: ColoredItemHandlerInfo) {
        this.information.add(info)
    }

    override fun getSidesForColor(color: EnumDyeColor): List<EnumFacing> {
        val list: List<EnumFacing>

        if (this.facesConfig.containsKey(color)) {
            list = this.facesConfig[color]!!
        } else {
            list = Lists.newArrayList<EnumFacing>()
        }

        return list
    }

    override fun setSidesForColor(color: EnumDyeColor, sides: List<EnumFacing>) {
        this.facesConfig.put(color, sides.toMutableList())
        this.updated()
    }

    override fun serializeNBT(): NBTTagList {
        val list = NBTTagList()

        val keys = this.facesConfig.keys.toTypedArray()
        for (k in keys.indices) {
            val nbt = NBTTagCompound()

            nbt.setInteger("color", keys[k].metadata)
            val sides = NBTTagList()
            for (facing in this.facesConfig[keys[k]]!!) {
                sides.appendTag(NBTTagInt(facing.index))
            }

            nbt.setTag("sides", sides)
            list.appendTag(nbt)
        }

        return list
    }

    override fun deserializeNBT(nbt: NBTTagList) {
        this.facesConfig.clear()
        for (i in 0..nbt.tagCount() - 1) {
            val item = nbt.getCompoundTagAt(i)
            val color = EnumDyeColor.byMetadata(item.getInteger("color"))
            val sides = Lists.newArrayList<EnumFacing>()
            val list = item.getTagList("sides", Constants.NBT.TAG_INT)
            for (j in 0..list.tagCount() - 1) {
                sides.add(EnumFacing.getFront(list.getIntAt(j)))
            }
            this.facesConfig.put(color, sides)
        }
        this.updated()
    }

    protected open fun updated() {}

    override fun removeColoredInfo(color: EnumDyeColor) {
        this.information!!.removeIf { i -> i.color == color }
        this.facesConfig.remove(color)

        this.updated()
    }
}
