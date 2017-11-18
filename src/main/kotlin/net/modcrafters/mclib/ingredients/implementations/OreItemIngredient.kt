package net.modcrafters.mclib.ingredients.implementations

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.utils.copyWithSize

class OreItemIngredient(val oreKey: String, override val amount: Int): BaseItemIngredient() {
    override val itemStacks: List<ItemStack>
        get() = OreDictionary.getOres(this.oreKey).map { it.copyWithSize(this.amount) }
}
