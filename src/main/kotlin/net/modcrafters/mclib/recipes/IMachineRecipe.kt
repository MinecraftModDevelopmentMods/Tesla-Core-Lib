package net.modcrafters.mclib.recipes

import net.minecraftforge.registries.IForgeRegistryEntry
import net.modcrafters.mclib.ingredients.IMachineIngredient

interface IMachineRecipe<T: IMachineRecipe<T>> : IForgeRegistryEntry<T> {
    val inputs: Array<IMachineRecipeInput>

    val primaryOutput: IMachineIngredient
    val secondaryOutput: Array<IMachineIngredient>
}
