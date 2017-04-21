package net.ndrei.teslacorelib.items;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.List;

/**
 * Created by CF on 2017-02-17.
 */
public class GearStoneItem extends BaseGearItem {
    public GearStoneItem() {
        super("stone");
    }

    @Override
    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipes = super.getRecipes();

//        for (Block block : new Block[]{
//                Blocks.COBBLESTONE, Blocks.STONE, Blocks.MOSSY_COBBLESTONE, Blocks.STONEBRICK
        for(Object block : new Object[]{
                "cobblestone", Blocks.STONE, "stone", Blocks.MOSSY_COBBLESTONE, Blocks.STONEBRICK,
                Blocks.SANDSTONE, Blocks.RED_SANDSTONE
        }) {
            recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                    " w ", "wsw", " w ",
                    'w', block,
                    's', "gearWood" // TeslaCoreLib.gearWood
            ));

            recipes.add(new ShapedOreRecipe(new ItemStack(this, 1),
                    " w ", "wsw", " w ",
                    'w', block,
                    's', "stickWood" // Items.STICK
            ));
        }

        return recipes;
    }
}
