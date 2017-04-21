package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

/**
 * Created by CF on 2017-01-02.
 */
public class MachineCaseItem extends RegisteredItem {
    public MachineCaseItem() {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "machine_case");
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "xyx", "yzy", "xyx",
                'x', "ingotIron", // Items.IRON_INGOT,
                'y', "plankWood", // Blocks.PLANKS,
                'z', "blockRedstone" //  Blocks.REDSTONE_BLOCK
        );
    }
}