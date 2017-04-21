package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by CF on 2017-02-17.
 */
public class GearIronItem extends BaseGearItem {
    public GearIronItem() {
        super("iron");
    }

    @Override
    protected IRecipe getRecipe(){
        return new ShapedOreRecipe(new ItemStack(this, 1),
                " w ", "wsw", " w ",
                'w', "ingotIron", // Items.IRON_INGOT,
                's', "gearStone" // TeslaCoreLib.gearStone
        );
    }
}
