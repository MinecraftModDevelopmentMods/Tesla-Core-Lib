package net.ndrei.teslacorelib.annotations

import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.RegisteredBlock
import net.ndrei.teslacorelib.items.RegisteredItem
import net.ndrei.teslacorelib.render.ISelfRegisteringRenderer

/**
 * Created by CF on 2017-06-22.
 */
@Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
object AutoRegisterRecipesHandler: BaseAnnotationHandler<Any>({ it, _, _ ->
    val registry = GameRegistry.findRegistry(IRecipe::class.java)
    when (it) {
        is RegisteredItem -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        is RegisteredBlock -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        // else -> TeslaCoreLib.logger.error("Annotated class '${it.javaClass.canonicalName}' does not provide a recipe.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class) {
    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    fun registerRecipe(registry: IForgeRegistry<IRecipe>, recipe: IRecipe): ResourceLocation {
        if (recipe.registryName == null) {
            val output = recipe.recipeOutput
            val activeContainer = Loader.instance().activeModContainer()
            val baseLoc = ResourceLocation(activeContainer?.modId, output.item.registryName?.path)
            var recipeLoc = baseLoc
            var index = 0
            while (registry.containsKey(recipeLoc)) {
                recipeLoc = ResourceLocation(activeContainer?.modId, "${baseLoc.path}_${++index}")
            }
            recipe.registryName = recipeLoc
        }
        registry.register(recipe)
        return recipe.registryName!!
    }
}

@SideOnly(Side.CLIENT)
object AutoRegisterRendererHandler: BaseAnnotationHandler<Any>({ it, _, _ ->
    when (it) {
        is ISelfRegisteringRenderer -> it.registerRenderer()
        else -> TeslaCoreLib.logger.warn("Annotated class '${it.javaClass.canonicalName}' does not provide a renderer.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class/*, AutoRegisterFluid::class*/)
