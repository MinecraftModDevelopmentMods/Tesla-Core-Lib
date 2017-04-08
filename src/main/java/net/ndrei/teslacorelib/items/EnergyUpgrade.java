package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

/**
 * Created by CF on 2017-04-08.
 */
public class EnergyUpgrade extends BaseTieredAddon {
    private int tier;

    public EnergyUpgrade(int tier) {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "energy_tier" + tier);
        this.tier = tier;
    }

    @Override
    protected String getAddonFunction() {
        return "energy";
    }

    @Override
    protected int getTier() {
        return this.tier;
    }

    @Override
    public float getWorkEnergyMultiplier() {
        return 0.75f;
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return EnergyUpgrade.canBeAddedToMachine(machine) && super.canBeAddedTo(machine);
    }

    static boolean canBeAddedToMachine(SidedTileEntity machine) {
        return ((machine instanceof ElectricMachine) && (((ElectricMachine)machine).supportsEnergyUpgrades()));
    }

    @Override
    public void onAdded(ItemStack addon, SidedTileEntity machine) {
        super.onAdded(addon, machine);

        if (machine instanceof ElectricMachine) {
            ((ElectricMachine) machine).updateWorkEnergyCapacity();
        }
    }

    @Override
    public void onRemoved(ItemStack addon, SidedTileEntity machine) {
        super.onRemoved(addon, machine);

        if (machine instanceof ElectricMachine) {
            ((ElectricMachine) machine).updateWorkEnergyCapacity();
        }
    }
}
