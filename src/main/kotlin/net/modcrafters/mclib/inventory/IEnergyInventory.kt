package net.modcrafters.mclib.inventory

import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.modcrafters.mclib.ingredients.asIngredient

interface IEnergyInventory: IMachineInventory {
    fun getStoredEnergy(): IGenericEnergyStorage

    override val slots: Int get() = 1
    override fun getIngredient(slot: Int) = this.getStoredEnergy().asIngredient()
}
