package net.modcrafters.mclib.ingredients.implementations

import net.modcrafters.mclib.energy.IGenericEnergyStorage

class EnergyWrapperIngredient(private val energy: IGenericEnergyStorage): BaseEnergyIngredient() {
    override val amount: Int
        get() = this.energy.stored.toInt()
}
