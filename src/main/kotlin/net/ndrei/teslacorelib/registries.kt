package net.ndrei.teslacorelib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingyHandler
import net.ndrei.teslacorelib.annotations.AutoRegisterRecipesHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.compatibility.IBlockColorDelegate
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.items.RegisteredItem
import net.ndrei.teslacorelib.items.powders.ColoredPowderItem

/**
 * Created by CF on 2017-06-28.
 */
class MaterialInfo<T: IForgeRegistryEntry<T>>(val oreDictName: String, private val registerCallback: (registry: IForgeRegistry<T>) -> T) {
    fun getItems() = OreDictionary.getOres(this.oreDictName)
            .map { it.item }
            .toList()

    fun registerItem(registry: IForgeRegistry<T>): T {
        val item = this.registerCallback(registry)
        when (item) {
            is Item -> OreDictionary.registerOre(this.oreDictName, item)
            is Block -> OreDictionary.registerOre(this.oreDictName, item)
            else -> {
                TeslaCoreLib.logger.warn("Don't know how to register '$item' in ore dictionary.")
            }
        }
        return item
    }
}

object MaterialRegistries {
    private val registries = mutableListOf<MaterialRegistry<*>>()
    fun registerRegistry(registry: MaterialRegistry<*>) = this.registries.add(registry)
    fun getRegistries() = this.registries.toList()
}

abstract class MaterialRegistry<T: IForgeRegistryEntry<T>>(private val configFlag: String, private val oreDictify: (material: String) -> String) {
    private val materials = mutableMapOf<String, MaterialInfo<T>>()
    private val registeredMaterials = mutableListOf<T>()

    init {
        MaterialRegistries.registerRegistry(this) // YES, YES, I know this is bad!
    }

    fun addMaterial(material: String, registerCallback: (registry: IForgeRegistry<T>) -> T) {
        if (!this.materials.containsKey(material)) {
            val oreDictName = this.oreDictify(material)
            this.materials[oreDictName] = MaterialInfo(oreDictName, registerCallback)
        }
    }

    fun getMaterial(material: String) = this.materials[material]

    fun getMaterials() = this.materials.keys.toSet()

    fun registerMissing(registry: IForgeRegistry<T>) {
        if (this.configFlag.isNotBlank() && !TeslaCoreLib.modConfigFlags.getFlag(this.configFlag)) {
            // do not register this things!
            return
        }

        this.materials
                .values
                .filter { it.getItems().isEmpty() }
                .map {
                    val item = it.registerItem(registry)
                    this.registeredMaterials.add(item)
                    item
                }
    }

    fun postRegister(asm: ASMDataTable) {
        this.registeredMaterials
                .forEach {
                    if ((it is IItemColorDelegate) || (it is IBlockColorDelegate)) {
                        AutoRegisterColoredThingyHandler.handler(it, asm, null)
                    }
                    this.postProcessThing(it)
                }
    }

    open fun postProcessThing(item: T) {
    }
}

abstract class MaterialItemRegistry(configFlag: String, oreDictify: (material: String) -> String) : MaterialRegistry<Item>(configFlag, oreDictify) {
    fun addMaterial(material: String, item: RegisteredItem)
            = this.addMaterial(material, {registry ->
        registry.register(item)
        item.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(GameRegistry.findRegistry(IRecipe::class.java), it) }
        if (TeslaCoreLib.isClientSide) {
            item.registerRenderer()
        }
        item
    })
}

abstract class MaterialBlockRegistry(configFlag: String, oreDictify: (material: String) -> String) : MaterialRegistry<Block>(configFlag, oreDictify) {
    fun addMaterial(material: String, block: OrientedBlock<*>)
            = this.addMaterial(material, { registry ->
        registry.register(block)
        block.registerRecipe { AutoRegisterRecipesHandler.registerRecipe(GameRegistry.findRegistry(IRecipe::class.java), it) }
        if (TeslaCoreLib.isClientSide) {
            block.registerRenderer()
        }
        block
    })
}

object PowderRegistry : MaterialItemRegistry(TeslaCoreLibConfig.REGISTER_POWDERS, { "dust${it.capitalize()}" }) {
    override fun postProcessThing(item: Item) {
        if (item is ColoredPowderItem) {
            var stack = OreDictionary.getOres("ingot${item.materialName.capitalize()}").firstOrNull()
            if (stack == null) {
                stack = OreDictionary.getOres("gem${item.materialName.capitalize()}").firstOrNull()
            }
            if (stack != null) {
                FurnaceRecipes.instance().addSmelting(item, stack, 0.0f);
            }
        }
    }
}

object GearRegistry : MaterialItemRegistry(TeslaCoreLibConfig.REGISTER_GEARS, { "gear${it.capitalize()}" })
object SheetRegistry : MaterialItemRegistry(TeslaCoreLibConfig.REGISTER_SHEETS, { "plate${it.capitalize()}" })

@Target(AnnotationTarget.CLASS)
annotation class AfterAllModsRegistry

@Mod(modid = "${MOD_ID}_registries", version = MOD_VERSION, name = "$MOD_NAME Registries",
        acceptedMinecraftVersions = MOD_MC_VERSION,
        dependencies = "${MOD_DEPENDENCIES}after:*;",
        useMetadata = true,
        modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object TeslaCoreRegistries {
    private lateinit var asm: ASMDataTable

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        this.asm = event.asmData

        object : BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.preInit(asm)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        object : BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.init(asm)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        MaterialRegistries.getRegistries()
                .forEach {
                    it.postRegister(this.asm)
                }

        object: BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.postInit(asm)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }

    @SubscribeEvent
    fun registerBlocks(ev: RegistryEvent.Register<Block>) {
        object: BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.registerBlocks(asm, ev.registry)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)

        MaterialRegistries.getRegistries()
                .forEach {
                    when (it) {
                        is MaterialBlockRegistry -> it.registerMissing(ev.registry)
                        else -> { }
                    }
                }
    }

    @SubscribeEvent
    fun registerItems(ev: RegistryEvent.Register<Item>) {
        object: BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.registerItems(asm, ev.registry)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)

        MaterialRegistries.getRegistries()
                .forEach {
                    when (it) {
                        is MaterialItemRegistry -> it.registerMissing(ev.registry)
                        else -> { }
                    }
                }
    }

    @SubscribeEvent
    fun registerRecipes(ev: RegistryEvent.Register<IRecipe>) {
        object: BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.registerRecipes(asm, ev.registry)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }

    @SubscribeEvent
    fun registerRenderers(ev: ModelRegistryEvent) {
        object: BaseAnnotationHandler<IRegistryHandler>({ it, asm, _ ->
            it.registerRenderers(asm)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }
}
