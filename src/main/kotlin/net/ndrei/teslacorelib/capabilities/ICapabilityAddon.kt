package net.ndrei.teslacorelib.capabilities

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

interface ICapabilityAddon {
    fun hasCapability(sidedTileEntity: SidedTileEntity, capability: Capability<*>, facing: EnumFacing?, orientedFacing: EnumFacing?): Boolean? = null
    fun <T> getCapability(sidedTileEntity: SidedTileEntity, capability: Capability<T>, facing: EnumFacing?, orientedFacing: EnumFacing?): T? = null
}