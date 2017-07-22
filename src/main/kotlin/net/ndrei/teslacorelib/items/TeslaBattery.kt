package net.ndrei.teslacorelib.items

import cofh.redstoneflux.api.IEnergyContainerItem
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.compatibility.RFPowerProxy

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = RFPowerProxy.MODID, striprefs = true)
object TeslaBattery
    : RegisteredItem(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "battery"), IEnergyContainerItem {

    init {
        super
                .setHasSubtypes(true)
                .addPropertyOverride(ResourceLocation("power"), { stack, _, _ ->
                    val energy = stack?.getCapability(CapabilityEnergy.ENERGY, null)
                    if (energy == null) {
                        0.0f
                    }
                    else {
                        val thing = Math.round(energy.energyStored.toDouble() / energy.maxEnergyStored.toDouble() * 5)
                        Math.max(0, Math.min(5, thing)) * .2f
                    }
                })
    }

    override fun initCapabilities(stack: ItemStack?, nbt: NBTTagCompound?): ICapabilityProvider? {
        return object: ICapabilityProvider, INBTSerializable<NBTTagCompound> {
            private var storage = EnergyStorage(10000, 100, 100)

            @Suppress("UNCHECKED_CAST")
            override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
                    = if (capability == CapabilityEnergy.ENERGY) (this.storage as? T) else null

            override fun hasCapability(capability: Capability<*>, facing: EnumFacing?)
                    = capability == CapabilityEnergy.ENERGY

            override fun deserializeNBT(nbt: NBTTagCompound?) {
                val capacity = if ((nbt != null) && nbt.hasKey("capacity", Constants.NBT.TAG_INT))
                    nbt.getInteger("capacity")
                else 10000

                val stored = if ((nbt != null) && nbt.hasKey("stored", Constants.NBT.TAG_INT))
                    nbt.getInteger("stored")
                else 0

                val inputRate = /*if ((nbt != null) && nbt.hasKey("input_rate", Constants.NBT.TAG_INT))
                    nbt.getInteger("input_rate")
                else*/ 100

                val outputRate = /*if ((nbt != null) && nbt.hasKey("output_rate", Constants.NBT.TAG_INT))
                    nbt.getInteger("output_rate")
                else*/ 100

                this.storage = EnergyStorage(capacity, inputRate, outputRate, stored)
            }

            override fun serializeNBT(): NBTTagCompound {
                return NBTTagCompound().also {
                    it.setInteger("capacity", this.storage.maxEnergyStored)
                    it.setInteger("stored", this.storage.energyStored)
//                    it.setInteger("input_rate", 100)
//                    it.setInteger("output_rate", 100)
                }
            }
        }
    }

    override fun getItemStackLimit(stack: ItemStack?): Int {
        val energy = stack?.getCapability(CapabilityEnergy.ENERGY, null)
        return if ((energy == null) || (energy.energyStored == 0)) 16 else 1
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        val energy = stack?.getCapability(CapabilityEnergy.ENERGY, null)
        if (energy != null) {
            if (energy.energyStored > 0) {
                tooltip!!.add(ChatFormatting.AQUA.toString() + "Power: " + energy.energyStored + ChatFormatting.RESET + " of " + ChatFormatting.AQUA + energy.maxEnergyStored)
            } else {
                tooltip!!.add(ChatFormatting.RED.toString() + "EMPTY!")
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (this.isInCreativeTab(tab)) {
            subItems.add(ItemStack(this))
            val full = ItemStack(this)
            val energy = full.getCapability(CapabilityEnergy.ENERGY, null)
            if (energy != null) {
                var cycle = 0
                while(energy.canReceive() && (energy.maxEnergyStored > energy.energyStored) && (cycle++ < 100)) {
                    energy.receiveEnergy(energy.maxEnergyStored - energy.energyStored, false)
                }
            }
            subItems.add(full)
        }
    }

    //#region IEnergyContainerItem members

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun getMaxEnergyStored(container: ItemStack?): Int {
        val energy = container?.getCapability(CapabilityEnergy.ENERGY, null) ?: return 0
        return energy.maxEnergyStored
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun getEnergyStored(container: ItemStack?): Int {
        val energy = container?.getCapability(CapabilityEnergy.ENERGY, null) ?: return 0
        return energy.energyStored
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun extractEnergy(container: ItemStack?, maxExtract: Int, simulate: Boolean): Int {
        val energy = container?.getCapability(CapabilityEnergy.ENERGY, null) ?: return 0
        return energy.extractEnergy(maxExtract, simulate)
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun receiveEnergy(container: ItemStack?, maxReceive: Int, simulate: Boolean): Int {
        val energy = container?.getCapability(CapabilityEnergy.ENERGY, null) ?: return 0
        return energy.receiveEnergy(maxReceive, simulate)
    }

    //#endregion
}
