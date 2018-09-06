package net.modcrafters.mclib.registries

import net.minecraft.util.ResourceLocation
import net.minecraftforge.registries.IForgeRegistryModifiable
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.recipes.IMachineRecipe

interface IRecipeRegistry<T: IMachineRecipe<T>> {
    val registry: IForgeRegistryModifiable<T>?
    val registryName: ResourceLocation

    val isRegistrationCompleted: Boolean

    fun addRecipe(recipe: T, suffixDuplicates: Boolean = true) = this.also {
        val name = recipe.registryName
        if ((name != null) && (this.getRecipe(name) != null)) {
            var index = 0
            var newName: ResourceLocation
            do {
                index++
                newName = ResourceLocation(name.namespace, "${name.path}_$index")
            } while (this.getRecipe(newName) != null)
            recipe.registryName = newName
        }
        this.registry!!.register(recipe)
    }

    fun hasRecipe(filter: (T) -> Boolean) =
        this.registry?.values?.any(filter) ?: false

    fun findRecipes(filter: (T) -> Boolean) =
        this.registry?.values?.filter(filter) ?: listOf()

    fun findRecipe(filter: (T) -> Boolean) =
        this.registry?.values?.firstOrNull(filter)

    fun getRecipe(name: ResourceLocation) =
        this.registry?.getValue(name)

    fun getAllRecipes(): List<T> =
        this.registry?.values ?: listOf()

    fun removeRecipe(registration: ResourceLocation) {
        this.registry?.remove(registration)
    }

    fun removeRecipeByPrimaryOutput(primaryOutput: IMachineIngredient) {
        this.registry?.removeAll {
            it.primaryOutput.isSame(primaryOutput)
        }
    }

    fun removeRecipeByOutput(output: IMachineIngredient) {
        this.registry?.removeAll {
            it.primaryOutput.isSame(output) || (it.secondaryOutput.any { it.isSame(output) })
        }
    }
}
