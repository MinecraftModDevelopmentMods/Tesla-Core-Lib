package net.modcrafters.mclib.inventory.implementations

import net.modcrafters.mclib.energy.IGenericEnergyStorage
import net.modcrafters.mclib.ingredients.IEnergyIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.inventory.IEnergyInventory

class EnergyInventory(override val key: String, private val energy: IGenericEnergyStorage) : IEnergyInventory {
    override fun getStoredEnergy() = this.energy

    override fun extract(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean) =
        when (ingredient) {
            is IEnergyIngredient -> this.energy.takePower(ingredient.amount.toLong(), simulate)
            else -> 0
        }.toInt()
}
