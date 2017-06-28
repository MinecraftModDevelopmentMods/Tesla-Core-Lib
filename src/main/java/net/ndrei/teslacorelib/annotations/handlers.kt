package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-22.
 */

fun registerItems(asmData: ASMDataTable, registry: IForgeRegistry<Item>) {
    val all = asmData.getAll(AutoRegisterItem::class.java.canonicalName)

    all.forEach {
        val c = try {
            Class.forName(it.className)
        } catch (e: ClassNotFoundException) {
            TeslaCoreLib.logger.error("Annotated class '${it.className}' not found!", e)
            null
        }

        if (c != null) {
            @Suppress("UNCHECKED_CAST")
            c as Class<Item>
            val instance = if (c.kotlin.objectInstance != null) c.kotlin.objectInstance else c.getConstructor()?.newInstance()
            if (instance != null) {
                if (instance is RegisteredItem) {
                    instance.register(registry)
                }
                else if (instance is Item) {
                    registry.register(instance)
                }
            }
        }
    }
}

fun registerBlocks(asmData: ASMDataTable, blockRegistry: IForgeRegistry<Block>, itemRegistry: IForgeRegistry<Item>) {
    val all = asmData.getAll(AutoRegisterBlock::class.java.canonicalName)

    all.forEach {
        val c = try {
            Class.forName(it.className)
        } catch (e: ClassNotFoundException) {
            TeslaCoreLib.logger.error("Annotated class '${it.className}' not found!", e)
            null
        }

        if (c != null) {
            @Suppress("UNCHECKED_CAST")
            c as Class<Block>
            val instance = if (c.kotlin.objectInstance != null) c.kotlin.objectInstance else c.getConstructor()?.newInstance()
            if (instance != null) {
                if (instance is OrientedBlock<*>) {
                    instance.register(blockRegistry, itemRegistry)
                }
                else if (instance is Block) {
                    blockRegistry.register(instance)
                    itemRegistry.register(ItemBlock(instance))
                }
            }
        }
    }
}

fun registerRecipes(asmData: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
    val all = asmData.getAll(AutoRegisterItem::class.java.canonicalName).toMutableList();
    all.addAll(asmData.getAll(AutoRegisterBlock::class.java.canonicalName))

    fun registerRecipe(recipe: IRecipe): ResourceLocation {
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

    all.forEach {
        val c = try {
            Class.forName(it.className)
        } catch (e: ClassNotFoundException) {
            TeslaCoreLib.logger.error("Annotated class '${it.className}' not found!", e)
            null
        }

        if (c != null) {
            val instance = if (c.kotlin.objectInstance != null) c.kotlin.objectInstance else c.getConstructor()?.newInstance()
            if (instance != null) {
                if (instance is RegisteredItem) {
                    instance.registerRecipe { registerRecipe(it) }
                }
                else if (instance is OrientedBlock<*>) {
                    instance.registerRecipe { registerRecipe(it) }
                }
                else {
                    TeslaCoreLib.logger.error("Annotated class '${it.className}' does not provide a recipe.")
                }
            }
        }
    }
}

@SideOnly(Side.CLIENT)
fun registerRenderers(asmData: ASMDataTable) {
    val all = asmData.getAll(AutoRegisterItem::class.java.canonicalName).toMutableList()
    all.addAll(asmData.getAll(AutoRegisterBlock::class.java.canonicalName))

    all.forEach {
        val c = try {
            Class.forName(it.className)
        } catch (e: ClassNotFoundException) {
            TeslaCoreLib.logger.error("Annotated class '${it.className}' not found!", e)
            null
        }

        if (c != null) {
            val instance = if (c.kotlin.objectInstance != null) c.kotlin.objectInstance else c.getConstructor()?.newInstance()
            if (instance != null) {
                if (instance is OrientedBlock<*>) {
                    instance.registerRenderer()
                }
                else if (instance is RegisteredItem) {
                    instance.registerRenderer()
                }
                else {
                    TeslaCoreLib.logger.error("Annotated class '${it.className}' does not provide a renderer.")
                }
            }
        }
    }
}
