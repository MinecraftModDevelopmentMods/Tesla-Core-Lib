package net.ndrei.teslacorelib.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/**
 * Created by CF on 2016-12-13.
 */
public class RegisteredItem extends Item {
    public RegisteredItem(String modId, CreativeTabs tab, String registryName) {
        super();

        this.setRegistryName(modId, registryName);
        this.setUnlocalizedName(modId + "_" + registryName);
        if (tab != null) {
            this.setCreativeTab(tab);
        }

        IRecipe recipe = this.getRecipe();
        if (recipe != null) {
            CraftingManager.getInstance().addRecipe(recipe);
        }
    }

    protected IRecipe getRecipe() {
        return null;
    }
}
