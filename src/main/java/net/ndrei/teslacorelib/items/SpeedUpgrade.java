package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

/**
 * Created by CF on 2017-04-08.
 */
public class SpeedUpgrade extends BaseTieredAddon {
    private int tier;

    public SpeedUpgrade(int tier) {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "speed_tier" + tier);
        this.tier = tier;
    }

    @Override
    protected String getAddonFunction() {
        return "speed";
    }

    @Override
    protected int getTier() {
        return this.tier;
    }

    @Override
    public float getWorkEnergyMultiplier() {
        return 1.25f;
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        return SpeedUpgrade.canBeAddedToMachine(machine) && super.canBeAddedTo(machine);
    }

    static boolean canBeAddedToMachine(SidedTileEntity machine) {
        return ((machine instanceof ElectricMachine) && (((ElectricMachine) machine).supportsSpeedUpgrades()));
    }

    @Override
    public void onAdded(ItemStack addon, SidedTileEntity machine) {
        super.onAdded(addon, machine);

        if (machine instanceof ElectricMachine) {
            ((ElectricMachine) machine).updateWorkEnergyRate();
        }
    }

    @Override
    public void onRemoved(ItemStack addon, SidedTileEntity machine) {
        super.onRemoved(addon, machine);

        if (machine instanceof ElectricMachine) {
            ((ElectricMachine) machine).updateWorkEnergyRate();
        }
    }
}
