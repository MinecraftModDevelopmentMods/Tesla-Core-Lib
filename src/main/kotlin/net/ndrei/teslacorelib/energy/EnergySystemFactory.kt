package net.ndrei.teslacorelib.energy

import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.modcrafters.mclib.energy.IGenericEnergyStorage

object EnergySystemFactory {
    private fun<T, R> Array<T>.firstNonNull(predicate: (element: T) -> R?): R? {
        @Suppress("LoopToCallChain")
        for (item in this) {
            val result = predicate(item) ?: continue
            return result
        }
        return null
    }

    fun wrapTileEntity(te: TileEntity, side: EnumFacing) : IGenericEnergyStorage?
            = EnergySystem.ORDERED.firstNonNull { it.system.wrapTileEntity(te, side) }

    fun wrapItemStack(stack: ItemStack) : IGenericEnergyStorage?
            = EnergySystem.ORDERED.firstNonNull { it.system.wrapItemStack(stack) }

    fun isCapabilitySupported(capability: Capability<*>)
            = EnergySystem.ORDERED.any { it.system.hasCapability(capability) }

    fun<T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage) : T?
            = EnergySystem.ORDERED.firstNonNull { it.system.wrapCapability(capability, energy) }
}