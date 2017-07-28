package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.render.ISelfRegisteringRenderer

/**
 * Created by CF on 2017-07-07.
 */
abstract class RegisteredBlock(modId: String, tab: CreativeTabs?, registryName: String, material: Material)
    : Block(material), ISelfRegisteringBlock, ISelfRegisteringRenderer {
    init {
        this.setRegistryName(modId, registryName)
        this.unlocalizedName = modId + "." + registryName
        if (tab != null) {
            this.setCreativeTab(tab)
        }
    }

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        registry.register(this)
    }

    override fun registerItem(registry: IForgeRegistry<Item>) {
        val item = ItemBlock(this)
        item.registryName = this.registryName
        registry.register(item)
    }

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    open fun registerRecipe(registry: (recipe: IRecipe) -> ResourceLocation) = this.recipes.forEach { registry(it) }

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    protected open val recipe: IRecipe?
        get() = null

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    protected open val recipes: List<IRecipe>
        get() {
            val recipe = this.recipe
            return if (recipe != null) listOf(recipe) else listOf()
        }

    @SideOnly(Side.CLIENT)
    override fun registerRenderer() {
        this.registerItemBlockRenderer()
    }

    @SideOnly(Side.CLIENT)
    protected open fun registerItemBlockRenderer() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                ModelResourceLocation(this.registryName!!, "inventory")
        )
    }
}