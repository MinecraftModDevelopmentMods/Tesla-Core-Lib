package net.ndrei.teslacorelib.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;

/**
 * Created by CF on 2017-01-02.
 */
public class BaseAddonItem extends BaseAddon {
    public BaseAddonItem() {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "base_addon");
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "xyx", "xxx", "xyx",
                'x', Items.PAPER,
                'y', Items.REDSTONE);
    }
}