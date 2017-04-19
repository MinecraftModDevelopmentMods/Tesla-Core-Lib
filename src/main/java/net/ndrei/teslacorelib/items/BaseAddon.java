package net.ndrei.teslacorelib.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

/**
 * Created by CF on 2017-03-07.
 */
public abstract class BaseAddon extends RegisteredItem {
    @SuppressWarnings("WeakerAccess")
    protected BaseAddon(String modId, CreativeTabs tab, String registryName) {
        super(modId, tab, registryName);
    }

    @SuppressWarnings("unused")
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return false;
    }

    @SuppressWarnings("unused")
    public void onAdded(ItemStack addon, SidedTileEntity machine) {
        // TeslaCoreLib.logger.info("Addon " + this.getClass().getSimpleName() + " added to " + ((machine == null) ? "n/a" : machine.getClass().getSimpleName()));
    }

    @SuppressWarnings("unused")
    public void onRemoved(ItemStack addon, SidedTileEntity machine) {
        // TeslaCoreLib.logger.info("Addon " + this.getClass().getSimpleName() + " removed from " + ((machine == null) ? "n/a" : machine.getClass().getSimpleName()));
    }

    public boolean isValid(SidedTileEntity machine) {
        return true;
    }

    public float getWorkEnergyMultiplier() {
        return 1.0f;
    }
}
