package net.ndrei.teslacorelib.annotations

import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.RegisteredBlock
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-22.
 */
@Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
object AutoRegisterRecipesHandler: BaseAnnotationHandler<Any>({ it, _, _ ->
    val registry = GameRegistry.findRegistry(IRecipe::class.java)
    when (it) {
        is RegisteredItem -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        is RegisteredBlock -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        else -> TeslaCoreLib.logger.error("Annotated class '${it.javaClass.canonicalName}' does not provide a recipe.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class) {
    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    fun registerRecipe(registry: IForgeRegistry<IRecipe>, recipe: IRecipe): ResourceLocation {
        if (recipe.registryName == null) {
            val output = recipe.recipeOutput
            val activeContainer = Loader.instance().activeModContainer()
            val baseLoc = ResourceLocation(activeContainer?.modId, output.item.registryName?.resourcePath)
            var recipeLoc = baseLoc
            var index = 0
            while (registry.containsKey(recipeLoc)) {
                recipeLoc = ResourceLocation(activeContainer?.modId, "${baseLoc.resourcePath}_${++index}")
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
        is RegisteredBlock -> it.registerRenderer()
        is RegisteredItem -> it.registerRenderer()
        else -> {
            try {
                val method = it.javaClass.getMethod("registerRenderer")
                method.invoke(it)
            }
            catch (t: Throwable) {
                TeslaCoreLib.logger.error("Annotated class '${it.javaClass.canonicalName}' does not provide a renderer.")
            }
        }
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class/*, AutoRegisterFluid::class*/)

// because just two 'Handler's are never enough
object AnnotationPreInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationPreInitHandler::class)

// because just two 'Handler's are never enough
object AnnotationInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationInitHandler::class)

// because just two 'Handler's are never enough
object AnnotationPostInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationPostInitHandler::class)

fun processPreInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
    // not registering these as handlers to make sure they are registered first and in order (if that matters?)
//    AutoRegisterItemHandler.process(asm, container)
    AutoRegisterFluidHandler.process(asm, container)
//    AutoRegisterBlockHandler.process(asm, container)

//    if (TeslaCoreLib.isClientSide) {
//        AutoRegisterRendererHandler.process(asm, container)
//    }

    AnnotationPreInitHandlerHandlerHandler.process(asm, container)
}

fun processInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
//    AutoRegisterRecipesHandler.process(asm, container)

    AnnotationInitHandlerHandlerHandler.process(asm, container)
}

fun processPostInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
    AnnotationPostInitHandlerHandlerHandler.process(asm, container)
}