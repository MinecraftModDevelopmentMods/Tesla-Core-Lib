package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

/**
 * Created by CF on 2017-04-08.
 */
public class SpeedUpgradeTier1 extends SpeedUpgrade {
    public SpeedUpgradeTier1() {
        super(1);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "rgr", "rbr", "rgr",
                'b', TeslaCoreLib.baseAddon,
                'r', "dustRedstone", //  Items.REDSTONE,
                'g', "gearGold" // TeslaCoreLib.gearGold
        );
    }
}
