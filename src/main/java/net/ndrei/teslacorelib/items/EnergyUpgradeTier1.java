package net.ndrei.teslacorelib.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

/**
 * Created by CF on 2017-04-08.
 */
public class EnergyUpgradeTier1 extends EnergyUpgrade {
    public EnergyUpgradeTier1() {
        super(1);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                " g ", "rbr", "rrr",
                'b', TeslaCoreLib.baseAddon,
                'r', Items.REDSTONE,
                'g', TeslaCoreLib.gearGold);
    }
}
