package net.modcrafters.mclib.recipes.implementations

import net.minecraft.util.ResourceLocation
import net.modcrafters.mclib.recipes.IMachineRecipe

abstract class BaseRecipeEntry<T: BaseRecipeEntry<T>>(
    private val type: Class<T>,
    private var registryName: ResourceLocation? = null)
    : IMachineRecipe<T> {

    override final fun getRegistryType() = this.type
    override final fun getRegistryName() = this.registryName
    override final fun setRegistryName(name: ResourceLocation?): T = this.type.cast(this).also { it.registryName = name }
}
