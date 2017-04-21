package net.ndrei.teslacorelib.items;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

import java.util.List;

/**
 * Created by CF on 2017-04-08.
 */
public class EnergyUpgradeTier2 extends EnergyUpgrade {
    public EnergyUpgradeTier2() {
        super(2);
    }

    @Override
    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipes = Lists.newArrayList();

        recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                " g ", "rbr", "rrr",
                'b', TeslaCoreLib.baseAddon,
                'r', "dustRedstone", // Items.REDSTONE,
                'g', "gearDiamond" // TeslaCoreLib.gearDiamond)
        ));

        recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                " d ", "dbd", " d ",
                'b', TeslaCoreLib.energyUpgradeTier1,
                'd', "gemDiamond" // Items.DIAMOND
        ));

        return recipes;
    }
}
