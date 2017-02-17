package net.ndrei.teslacorelib.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.Utils;

/**
 * Created by CF on 2017-02-17.
 */
public abstract class BaseGearItem extends RegisteredItem {
    private String materialName;

    public BaseGearItem(String materialName) {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "gear_" + materialName.toLowerCase());
        this.materialName = materialName;
    }

    @Override
    public void register() {
        super.register();

        OreDictionary.registerOre("gear" + Utils.capitalizeFirstLetter(this.materialName), this);
        OreDictionary.registerOre("gear_" + this.materialName.toLowerCase(), this);
        OreDictionary.registerOre(this.materialName.toLowerCase() + "Gear", this);
        OreDictionary.registerOre(this.materialName.toLowerCase() + "_gear", this);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 16;
    }
}
