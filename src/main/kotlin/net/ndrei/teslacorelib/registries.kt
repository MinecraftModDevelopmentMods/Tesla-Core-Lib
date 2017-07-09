package net.ndrei.teslacorelib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.IForgeRegistryEntry
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingyHandler
import net.ndrei.teslacorelib.annotations.AutoRegisterRecipesHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.compatibility.IBlockColorDelegate
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate
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

abstract class MaterialRegistry<T: IForgeRegistryEntry<T>>(private val oreDictify: (material: String) -> String) {
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

abstract class MaterialItemRegistry(oreDictify: (material: String) -> String) : MaterialRegistry<Item>(oreDictify) {
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

abstract class MaterialBlockRegistry(oreDictify: (material: String) -> String) : MaterialRegistry<Block>(oreDictify) {
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

object PowderRegistry : MaterialItemRegistry({ "dust${it.capitalize()}" }) {
    override fun postProcessThing(item: Item) {
        if (item is ColoredPowderItem) {
            val stack = OreDictionary.getOres("ore${item.materialName.capitalize()}")
                    .firstOrNull()
            if (stack != null) {
                FurnaceRecipes.instance().addSmelting(item, stack, 0.0f);
            }
        }
    }
}

object GearRegistry : MaterialItemRegistry({ "gear${it.capitalize()}" })
object SheetRegistry : MaterialItemRegistry({ "plate${it.capitalize()}" })

@Target(AnnotationTarget.CLASS)
annotation class AfterAllModsRegistry

interface IAfterAllModsRegistry {
    fun registerBeforeMaterials(asm : ASMDataTable) {}
    fun registerAfterMaterials(asm: ASMDataTable) {}
}

//object AfterAllModsRegistryHandler: BaseAnnotationHandler<IAfterAllModsRegistry>({ it, asm ->
//    it.process(asm)
//}, AfterAllModsRegistry::class)

@Mod(modid = TeslaCoreRegistries.MODID, version = TeslaCoreLib.VERSION, name = "Tesla Core Registries",
        dependencies = "after:*", useMetadata = true,
        modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
class TeslaCoreRegistries {
    private lateinit var asm: ASMDataTable

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        this.asm = event.asmData

        val itemRegistry = GameRegistry.findRegistry(Item::class.java)
        val blockRegistry = GameRegistry.findRegistry(Block::class.java)

        object : BaseAnnotationHandler<IAfterAllModsRegistry>({ it, asm, _ ->
            it.registerBeforeMaterials(asm)
        }, AfterAllModsRegistry::class) {}.process(event.asmData, null)

        MaterialRegistries.getRegistries()
                .forEach {
                    when (it) {
                        is MaterialItemRegistry -> it.registerMissing(itemRegistry)
                        is MaterialBlockRegistry -> it.registerMissing(blockRegistry)
                        else -> {
                            TeslaCoreLib.logger.warn("Don't know how to register missing entries from '$it'.")
                        }
                    }
                }
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        MaterialRegistries.getRegistries()
                .forEach {
                    it.postRegister(this.asm)
                }
        object: BaseAnnotationHandler<IAfterAllModsRegistry>({ it, asm, _ ->
            it.registerAfterMaterials(asm)
        }, AfterAllModsRegistry::class) {}.process(this.asm, null)
    }

    companion object {
        const val MODID = "teslacoreregistries"
    }
}
