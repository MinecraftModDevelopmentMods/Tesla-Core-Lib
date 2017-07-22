package net.ndrei.teslacorelib.energy

import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Loader

interface IEnergySystem {
    val ModId: String
    fun isAvailable(): Boolean = Loader.isModLoaded(this.ModId)

    fun hasCapability(capability: Capability<*>): Boolean
    fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T?

    fun wrapTileEntity(te: TileEntity, side: EnumFacing) : IGenericEnergyStorage?
    fun wrapItemStack(stack: ItemStack) : IGenericEnergyStorage?
}
