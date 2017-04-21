package net.ndrei.teslacorelib.items;

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
                'w', "plankWood", // Blocks.PLANKS,
                's', "stickWood" // Items.STICK
        ));
        recipes.add(new ShapedOreRecipe(new ItemStack(this, 4),
                " w ", "wsw", " w ",
                'w', "logWood", // Blocks.LOG,
                's', "stickWood" // Items.STICK
        ));
//        recipes.add(new ShapedOreRecipe(new ItemStack(this, 4),
//                " w ", "wsw", " w ",
//                'w', Blocks.LOG2,
//                's', "stickWood" // Items.STICK
//        ));

        return recipes;
    }
}
