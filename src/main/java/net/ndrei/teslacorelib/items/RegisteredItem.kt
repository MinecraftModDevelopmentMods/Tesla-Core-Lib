package net.ndrei.teslacorelib.items

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry

/**
 * Created by CF on 2017-06-22.
 */
abstract class RegisteredItem(modId: String, tab: CreativeTabs?, registryName: String) : Item() {
    init {
        this.setRegistryName(modId, registryName)
        this.unlocalizedName = modId + "." + registryName
        if (tab != null) {
            this.creativeTab = tab
        }
    }

    open fun register(registry: IForgeRegistry<Item>) {
        registry.register(this)
    }

    fun registerRecipe(registry: (recipe: IRecipe) -> ResourceLocation)
            = this.recipes.forEach { registry(it) }

    protected open val recipe: IRecipe?
        get() = null

    protected open val recipes: List<IRecipe>
        get() {
            val recipe = this.recipe
            return if (recipe != null) listOf(recipe) else listOf()
        }

    @SideOnly(Side.CLIENT)
    open fun registerRenderer() = ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(this.registryName!!, "inventory"))
}
