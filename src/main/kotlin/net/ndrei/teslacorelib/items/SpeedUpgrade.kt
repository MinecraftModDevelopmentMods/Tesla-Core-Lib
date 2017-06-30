package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.tileentities.ElectricMachine
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class SpeedUpgrade(override val tier: Int) : BaseTieredAddon(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "speed_tier" + tier) {
    override val addonFunction: String
        get() = "speed"

    override val workEnergyMultiplier: Float
        get() = 1.25f

    override fun canBeAddedTo(machine: SidedTileEntity): Boolean {
        return SpeedUpgrade.canBeAddedToMachine(machine) && super.canBeAddedTo(machine)
    }

    override fun onAdded(addon: ItemStack, machine: SidedTileEntity) {
        super.onAdded(addon, machine)
        (machine as? ElectricMachine)?.updateWorkEnergyRate()
    }

    override fun onRemoved(addon: ItemStack, machine: SidedTileEntity) {
        super.onRemoved(addon, machine)
        (machine as? ElectricMachine)?.updateWorkEnergyRate()
    }

    companion object {
        internal fun canBeAddedToMachine(machine: SidedTileEntity): Boolean {
            return machine is ElectricMachine && machine.supportsSpeedUpgrades()
        }
    }
}