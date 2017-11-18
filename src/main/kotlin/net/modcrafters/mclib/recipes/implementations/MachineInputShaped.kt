package net.modcrafters.mclib.recipes.implementations

import net.modcrafters.mclib.ingredients.IPositionedIngredient
import net.modcrafters.mclib.inventory.IMachineInventory
import net.modcrafters.mclib.recipes.IMachineRecipeShaped

class MachineInputShaped(key: String, override val width: Int, override val height: Int, override val ingredients: Array<IPositionedIngredient>)
    : MachineInput(key), IMachineRecipeShaped {

    override fun getIngredient(column: Int, row: Int) =
        this.ingredients.firstOrNull {
            it.slot == column + (row * this.width)
        }?.getIngredient()

    override fun isMatch(inventory: IMachineInventory) {
    }

    override fun process(inventory: IMachineInventory, simulate: Boolean) {
    }
}