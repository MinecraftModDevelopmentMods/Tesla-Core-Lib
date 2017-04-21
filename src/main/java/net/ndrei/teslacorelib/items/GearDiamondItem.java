package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

/**
 * Created by CF on 2017-02-17.
 */
public class GearDiamondItem extends BaseGearItem {
    public GearDiamondItem() {
        super("diamond");
    }

    @Override
    protected List<IRecipe> getRecipes(){
        List<IRecipe> recipes = super.getRecipes();

        recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                "iwi", "wsw", "iwi",
                'w', "gemDiamond", // Items.DIAMOND,
                'i', "ingotIron", // Items.IRON_INGOT,
                's', "gearStone" // TeslaCoreLib.gearStone
        ));
        recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                " w ", "wsw", " w ",
                'w', "gemDiamond", // Items.DIAMOND,
                's', "gearIron" // TeslaCoreLib.gearIron
        ));

        return recipes;
    }
}
