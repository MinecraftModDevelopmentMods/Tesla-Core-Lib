package net.modcrafters.mclib.registries

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.GenericEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.registries.*
import net.modcrafters.mclib.recipes.IMachineRecipe
import net.ndrei.teslacorelib.annotations.IRegistryHandler

abstract class BaseRecipeRegistry<T: IMachineRecipe<T>>(modId: String, registryName: String, private val type: Class<T>)
    : IRegistryHandler, IRecipeRegistry<T> {
    override val registryName = ResourceLocation(modId, registryName)

    override fun construct(asm: ASMDataTable) {
        super.construct(asm)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun registerRegistry(ev: RegistryEvent.NewRegistry) {
        RegistryBuilder<T>()
            .setName(registryName)
            .setMaxID(MAX_RECIPE_ID)
            .setType(this.type)
            .add(AddCallback(this.type))
            .add(ClearCallback(this.type))
            .disableSaving()
            .allowModification()
            .create()
    }

    class AddCallback<T : IForgeRegistryEntry<T>>(private val type: Class<T>) : IForgeRegistry.AddCallback<T> {
        override fun onAdd(owner: IForgeRegistryInternal<T>?, stage: RegistryManager?, id: Int, obj: T, oldObj: T?) {
            MinecraftForge.EVENT_BUS.post(EntryAddedEvent(this.type, obj))
        }
    }

    class ClearCallback<T : IForgeRegistryEntry<T>>(private val type: Class<T>) : IForgeRegistry.ClearCallback<T> {
        override fun onClear(owner: IForgeRegistryInternal<T>?, stage: RegistryManager?) {
            if (owner != null) {
                MinecraftForge.EVENT_BUS.post(RegistryClearEvent(this.type, owner))
            }
        }
    }

    class EntryAddedEvent<T : IForgeRegistryEntry<T>> internal constructor(type: Class<T>, val entry: T) : GenericEvent<T>(type)
    class RegistryClearEvent<T : IForgeRegistryEntry<T>> internal constructor(type: Class<T>, val registry: IForgeRegistry<T>) : GenericEvent<T>(type)

    class DefaultRegistrationCompletedEvent<T: IMachineRecipe<T>> internal constructor(type: Class<T>, val registry: IRecipeRegistry<T>): GenericEvent<T>(type)

    override final var isRegistrationCompleted = false
        private set

    protected fun registrationCompleted() {
        MinecraftForge.EVENT_BUS.post(DefaultRegistrationCompletedEvent(this.type, this))
        this.isRegistrationCompleted = true
    }

    override val registry get(): IForgeRegistryModifiable<T>? = GameRegistry.findRegistry(this.type) as? IForgeRegistryModifiable<T>

    companion object {
        const val MAX_RECIPE_ID = Integer.MAX_VALUE shr 5
    }
}
