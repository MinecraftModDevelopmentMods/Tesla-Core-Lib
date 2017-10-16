package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.registries.IForgeRegistry

/**
 * Created by CF on 2017-07-15.
 */
@Target(AnnotationTarget.CLASS)
annotation class RegistryHandler(vararg val configFlags: String)

interface IRegistryHandler {
    fun construct(asm: ASMDataTable) {}
    fun preInit(asm: ASMDataTable) {}
    fun init(asm: ASMDataTable) {}
    fun postInit(asm: ASMDataTable) {}

    fun registerItems(asm: ASMDataTable, registry: IForgeRegistry<Item>) {}
    fun registerBlocks(asm: ASMDataTable, registry: IForgeRegistry<Block>) {}
    fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {}
    fun registerRenderers(asm: ASMDataTable) {}
}
