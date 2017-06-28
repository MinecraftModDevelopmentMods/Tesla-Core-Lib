package net.ndrei.teslacorelib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.registerBlocks
import net.ndrei.teslacorelib.annotations.registerItems
import net.ndrei.teslacorelib.annotations.registerRecipes
import net.ndrei.teslacorelib.annotations.registerRenderers
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.gui.TeslaCoreGuiProxy

/**
 * Created by CF on 2017-06-27.
 */
open class CommonProxy {
    protected lateinit var asm: ASMDataTable

    @SuppressWarnings("unused")
    open fun preInit(ev: FMLPreInitializationEvent) {
        this.asm = ev.asmData

        val itemRegistry = GameRegistry.findRegistry(Item::class.java)
        val blockRegistry = GameRegistry.findRegistry(Block::class.java)
        val recipeRegistry = GameRegistry.findRegistry(IRecipe::class.java)

        registerItems(this.asm, itemRegistry)
        registerBlocks(this.asm, blockRegistry, itemRegistry)
        registerRecipes(this.asm, recipeRegistry)

        TeslaCoreCapabilities.register()
        NetworkRegistry.INSTANCE.registerGuiHandler(TeslaCoreLib.instance, TeslaCoreGuiProxy())
    }

    @SuppressWarnings("unused")
    open fun init(ev: FMLInitializationEvent) {
    }

    @SuppressWarnings("unused")
    open fun postInit(ev: FMLPostInitializationEvent) {
    }
}

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy: CommonProxy() {
    override fun preInit(ev: FMLPreInitializationEvent) {
        super.preInit(ev)

        registerRenderers(this.asm)
    }
}

@Suppress("unused")
@SideOnly(Side.SERVER)
class ServerProxy: CommonProxy()