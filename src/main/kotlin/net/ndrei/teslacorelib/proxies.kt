package net.ndrei.teslacorelib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.*
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.gui.TeslaCoreGuiProxy
import net.ndrei.teslacorelib.render.selfrendering.SelfRenderingBlocksRegistry

/**
 * Created by CF on 2017-06-27.
 */
abstract class BaseProxy(val side: Side) {
    protected lateinit var asm: ASMDataTable
    protected var loadLevel = ProxyLoadLevel.INITIAL
        private set

    private val container
        get() = net.minecraftforge.fml.common.Loader.instance().activeModContainer()

    fun testLoadLevel(level: ProxyLoadLevel, caller: String) {
        if (this.loadLevel.ordinal < level.ordinal) {
            TeslaCoreLib.logger.error("********************************************************************************")
            TeslaCoreLib.logger.error("* You should not access '$caller' before ${level.info}.".padEnd(79, ' ') + "*")
            TeslaCoreLib.logger.error("*     culprit mod: ${Loader.instance().activeModContainer()?.name ?: "n/a"}.".padEnd(79, ' ') + "*")
            TeslaCoreLib.logger.error("********************************************************************************")
            val ex = SecurityException("Error accessing $caller before ${level.info}")
            TeslaCoreLib.logger.warn("${ex.message}:", ex)
            // throw ex
        }
    }

    private fun processRegistryHandlers(handler: (handler: IRegistryHandler) -> Unit) {
        object: BaseAnnotationHandler<IRegistryHandler>({ it, _, _ ->
            handler(it)
        }, RegistryHandler::class) {}.process(this.asm, this.container)
    }

    @SuppressWarnings("unused")
    open fun preInit(ev: FMLPreInitializationEvent) {
        this.asm = ev.asmData
        MinecraftForge.EVENT_BUS.register(this)
        this.loadLevel = ProxyLoadLevel.PRE_INIT

        processPreInitAnnotations(ev.asmData, this.container)
        this.processRegistryHandlers { it.preInit(this.asm) }
    }

    @SuppressWarnings("unused")
    open fun init(ev: FMLInitializationEvent) {
        this.loadLevel = ProxyLoadLevel.INIT

        processInitAnnotations(this.asm, this.container)
        this.processRegistryHandlers { it.init(this.asm) }
    }

    @SuppressWarnings("unused")
    open fun postInit(ev: FMLPostInitializationEvent) {
        this.loadLevel = ProxyLoadLevel.POST_INIT

        processPostInitAnnotations(this.asm, this.container)
        this.processRegistryHandlers { it.postInit(this.asm) }
    }

    @SubscribeEvent
    fun registerBlocks(ev: RegistryEvent.Register<Block>) {
        this.loadLevel = ProxyLoadLevel.BLOCKS

        AutoRegisterBlockHandler.process(this.asm, this.container)
        this.processRegistryHandlers { it.registerBlocks(this.asm, ev.registry) }
    }

    @SubscribeEvent
    fun registerItems(ev: RegistryEvent.Register<Item>) {
        this.loadLevel = ProxyLoadLevel.ITEMS

        AutoRegisterItemHandler.process(this.asm, this.container)
        this.processRegistryHandlers { it.registerItems(this.asm, ev.registry) }
    }

    @SubscribeEvent
    fun registerRecipes(ev: RegistryEvent.Register<IRecipe>) {
        this.loadLevel = ProxyLoadLevel.RECIPES

        AutoRegisterRecipesHandler.process(this.asm, this.container)
        this.processRegistryHandlers { it.registerRecipes(this.asm, ev.registry) }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    fun registerModel(ev: ModelRegistryEvent) {
        SelfRenderingBlocksRegistry.initialize(this.asm, this.container)
        AutoRegisterRendererHandler.process(this.asm, this.container)
        this.processRegistryHandlers { it.registerRenderers(this.asm) }
    }
}

abstract class CommonProxy(side: Side): BaseProxy(side) {
    override fun preInit(ev: FMLPreInitializationEvent) {
        super.preInit(ev)

        TeslaCoreCapabilities.register()
        NetworkRegistry.INSTANCE.registerGuiHandler(TeslaCoreLib, TeslaCoreGuiProxy())
    }
}

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy: CommonProxy(Side.CLIENT)

@Suppress("unused")
@SideOnly(Side.SERVER)
class ServerProxy: CommonProxy(Side.SERVER)

enum class ProxyLoadLevel(val info: String) {
    INITIAL("[n/a]"),
    PRE_INIT("mod pre initialization"),
    BLOCKS("blocks registry event"),
    ITEMS("items registry event"),
    RECIPES("recipes registry event"),
    INIT("mod initialization"),
    POST_INIT("mod post initialization");
}