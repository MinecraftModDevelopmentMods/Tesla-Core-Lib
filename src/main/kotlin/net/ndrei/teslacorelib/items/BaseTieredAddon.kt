package net.ndrei.teslacorelib.items

import net.minecraft.creativetab.CreativeTabs
import net.ndrei.teslacorelib.tileentities.SidedTileEntity
import java.util.*

/**
 * Created by CF on 2017-06-27.
 */
open class BaseTieredAddon(modId: String, tab: CreativeTabs, registryName: String)
    : BaseAddon(modId, tab, registryName) {
    protected fun hasSameFunction(other: BaseTieredAddon?): Boolean {
        return other != null && (this.javaClass.isAssignableFrom(other.javaClass) || this.addonFunction == other.addonFunction)
    }

    protected open val addonFunction: String
        get() = this.registryName!!.toString()

    protected open val tier: Int
        get() = 1

    fun isTierValid(machine: SidedTileEntity, tier: Int, ignoreSameTier: Boolean): Boolean {
        if (tier == 1 && ignoreSameTier) {
            return true
        }

        val tiers = HashMap<Int, BaseAddon>()

        val addons = machine.addons
        for (addon in addons) {
            if (addon !is BaseTieredAddon) {
                break
            }
            val tiered = addon

            if (this.hasSameFunction(tiered)) {
                if (tiered.tier == tier && !ignoreSameTier) {
                    // already has an addon with same tier and function
                    return false
                }

                tiers.put(tiered.tier, tiered)
            }
        }

        // missing an addon with an inferior tier
        return ((tier == 1) && (tiers.count() == 0)) || (1..tier - 1).any { tiers.containsKey(it) }
    }

    override fun canBeAddedTo(machine: SidedTileEntity): Boolean {
        return this.isTierValid(machine, this.tier, false)
    }

    override fun isValid(machine: SidedTileEntity): Boolean {
        return super.isValid(machine) && this.isTierValid(machine, this.tier, true)
    }
}
