package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.tileentities.ElectricMachine
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class EnergyUpgrade(override val tier: Int) : BaseTieredAddon(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "energy_tier" + tier) {
    override val addonFunction: String
        get() = "energy"

    override val workEnergyMultiplier: Float
        get() = 0.75f

    override fun canBeAddedTo(machine: SidedTileEntity): Boolean {
        return EnergyUpgrade.canBeAddedToMachine(machine) && super.canBeAddedTo(machine)
    }

    override fun onAdded(addon: ItemStack, machine: SidedTileEntity) {
        super.onAdded(addon, machine)
        (machine as? ElectricMachine)?.updateWorkEnergyCapacity()
    }

    override fun onRemoved(addon: ItemStack, machine: SidedTileEntity) {
        super.onRemoved(addon, machine)
        (machine as? ElectricMachine)?.updateWorkEnergyCapacity()
    }

    companion object {
        internal fun canBeAddedToMachine(machine: SidedTileEntity): Boolean {
            return machine is ElectricMachine && machine.supportsEnergyUpgrades()
        }
    }
}