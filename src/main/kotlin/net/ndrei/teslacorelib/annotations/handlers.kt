package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-22.
 */

object AutoRegisterItemHandler : BaseAnnotationHandler<Item>({ it, _ ->
    val registry = GameRegistry.findRegistry(Item::class.java)
    when (it) {
        is RegisteredItem -> it.register(registry)
        else -> registry.register(it)
    }
}, AutoRegisterItem::class)

object AutoRegisterBlockHandler: BaseAnnotationHandler<Block>({ it, _ ->
    val itemRegistry = GameRegistry.findRegistry(Item::class.java)
    val blockRegistry = GameRegistry.findRegistry(Block::class.java)
    when (it) {
        is OrientedBlock<*> -> it.register(blockRegistry, itemRegistry)
        else -> {
            blockRegistry.register(it)
            val item = ItemBlock(it)
            item.registryName = it.registryName
            itemRegistry.register(item)
        }
    }
}, AutoRegisterBlock::class)

object AutoRegisterRecipesHandler: BaseAnnotationHandler<Any>({ it, _ ->
    val registry = GameRegistry.findRegistry(IRecipe::class.java)
    when (it) {
        is RegisteredItem -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        is OrientedBlock<*> -> it.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(registry, it) }
        else -> TeslaCoreLib.logger.error("Annotated class '${it.javaClass.canonicalName}' does not provide a recipe.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class) {
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
object AutoRegisterRendererHandler: BaseAnnotationHandler<Any>({ it, _ ->
    when (it) {
        is OrientedBlock<*> -> it.registerRenderer()
        is RegisteredItem -> it.registerRenderer()
        else -> TeslaCoreLib.logger.error("Annotated class '${it.javaClass.canonicalName}' does not provide a renderer.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class)

// because just two 'Handler's are never enough
object AnnotationPreInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm ->
    it.process(asm)
}, AnnotationPreInitHandler::class)

// because just two 'Handler's are never enough
object AnnotationInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm ->
    it.process(asm)
}, AnnotationInitHandler::class)

@Suppress("unused")
@AnnotationPostInitHandler
object AutoRegisterColoredThingyHandler: BaseAnnotationHandler<Any>({ it, _ ->
    if (TeslaCoreLib.isClientSide) {
        when (it) {
            is IItemColor -> when (it) {
                is Item -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(it, it)
                is Block -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(it, it)
                else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an Item nor a Block.")
            }
            is IBlockColor -> Minecraft.getMinecraft().blockColors.registerBlockColorHandler(it, it as Block)
            else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an IItemColor nor a IBlockColor.")
        }
    }
}, AutoRegisterColoredThingy::class)

// because just two 'Handler's are never enough
object AnnotationPostInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm ->
    it.process(asm)
}, AnnotationPostInitHandler::class)

fun processPreInitAnnotations(asm: ASMDataTable) {
    // not registering these as handlers to make sure they are registered first and in order (if that matters?)
    AutoRegisterItemHandler.process(asm)
    AutoRegisterBlockHandler.process(asm)
    AutoRegisterRecipesHandler.process(asm)

    if (TeslaCoreLib.isClientSide) {
        AutoRegisterRendererHandler.process(asm)
    }

    AnnotationPreInitHandlerHandlerHandler.process(asm)
}

fun processInitAnnotations(asm: ASMDataTable) {
    AnnotationInitHandlerHandlerHandler.process(asm)
}

fun processPostInitAnnotations(asm: ASMDataTable) {
    AnnotationPostInitHandlerHandlerHandler.process(asm)
}