package net.ndrei.teslacorelib.items;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

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
    }

    public void register() {
        GameRegistry.register(this);

        List<IRecipe> recipes = this.getRecipes();
        if (recipes != null) {
            recipes.forEach(recipe -> {
                if (recipe != null) {
                    CraftingManager.getInstance().addRecipe(recipe);
                }
            });
        }
    }

    protected IRecipe getRecipe() {
        return null;
    }

    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipes = Lists.newArrayList();

        IRecipe recipe = this.getRecipe();
        if (recipe != null)
            recipes.add(recipe);

        return recipes;
    }

    public void registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                new ModelResourceLocation(this.getRegistryName(), "inventory")
        );
    }
}
