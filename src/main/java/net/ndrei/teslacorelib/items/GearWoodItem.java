package net.ndrei.teslacorelib.items;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

/**
 * Created by CF on 2017-02-17.
 */
public class GearWoodItem extends BaseGearItem {
    public GearWoodItem() {
        super("wood");
    }

    @Override
    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipes = super.getRecipes();

        recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                " w ", "wsw", " w ",
                'w', Blocks.PLANKS,
                's', Items.STICK));
        recipes.add(new ShapedOreRecipe(new ItemStack(this, 4),
                " w ", "wsw", " w ",
                'w', Blocks.LOG,
                's', Items.STICK));
        recipes.add(new ShapedOreRecipe(new ItemStack(this, 4),
                " w ", "wsw", " w ",
                'w', Blocks.LOG2,
                's', Items.STICK));

        return recipes;
    }
}
