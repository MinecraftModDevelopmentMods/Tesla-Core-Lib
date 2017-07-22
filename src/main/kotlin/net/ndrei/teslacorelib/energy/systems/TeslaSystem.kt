package net.ndrei.teslacorelib.energy.systems

import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
import net.darkhax.tesla.capability.TeslaCapabilities
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.ndrei.teslacorelib.energy.IEnergySystem
import net.ndrei.teslacorelib.energy.IGenericEnergyStorage

object TeslaSystem : IEnergySystem {
    const val MODID = "tesla"

    override val ModId get() = MODID

    override fun hasCapability(capability: Capability<*>)
            = this.isAvailable() && ((capability == TeslaCapabilities.CAPABILITY_CONSUMER)
            || (capability == TeslaCapabilities.CAPABILITY_HOLDER)
            || (capability == TeslaCapabilities.CAPABILITY_PRODUCER))

    @Suppress("UNCHECKED_CAST")
    override fun <T> wrapCapability(capability: Capability<T>, energy: IGenericEnergyStorage): T? {
        if (!this.isAvailable()) {
            return null
        }

        return when (capability) {
            TeslaCapabilities.CAPABILITY_HOLDER -> WrapperHolder(energy) as? T
            TeslaCapabilities.CAPABILITY_PRODUCER -> WrapperProducer(energy) as? T
            TeslaCapabilities.CAPABILITY_CONSUMER -> WraperConsumer(energy) as? T
            else -> null
        }
    }

    override fun wrapTileEntity(te: TileEntity, side: EnumFacing): IGenericEnergyStorage? {
        if (this.isAvailable() && (te.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side)
                || te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side)
                || te.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)
                )) {
            return ReverseWrapper(te, side)
        }
        return null
    }

    override fun wrapItemStack(stack: ItemStack): IGenericEnergyStorage? {
        if (this.isAvailable() && !stack.isEmpty && (
                stack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
                || stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null)
                || stack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)
                )) {
            return ReverseWrapper(stack, null)
        }
        return null
    }

    class WraperConsumer(val energy: IGenericEnergyStorage) : ITeslaConsumer {
        override fun givePower(power: Long, simulated: Boolean)
                = this.energy.givePower(power, simulated)
    }

    class WrapperProducer(val energy: IGenericEnergyStorage) : ITeslaProducer {
        override fun takePower(power: Long, simulated: Boolean)
                = this.energy.takePower(power, simulated)
    }

    class WrapperHolder(val energy: IGenericEnergyStorage) : ITeslaHolder {
        override fun getCapacity() = this.energy.capacity
        override fun getStoredPower() = this.energy.stored
    }

    class ReverseWrapper(provider: ICapabilityProvider, side: EnumFacing?): IGenericEnergyStorage {
        private val holder: ITeslaHolder?
                = if (provider.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, side))
            provider.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, side)
        else null

        private val consumer: ITeslaConsumer?
                = if (provider.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side))
            provider.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side)
        else null

        private val producer: ITeslaProducer?
                = if (provider.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side))
            provider.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, side)
        else null

        override val capacity get() = this.holder?.capacity ?: 0L // assume 0 :S
        override val stored get() = this.holder?.storedPower ?: 0L // assume 0 :S

        override val canGive get() = (this.consumer != null)
        override fun givePower(power: Long, simulated: Boolean)
                = this.consumer?.givePower(power, simulated) ?: 0L

        override val canTake get() = (this.producer != null)
        override fun takePower(power: Long, simulated: Boolean)
                = this.producer?.takePower(power, simulated) ?: 0L
    }
}