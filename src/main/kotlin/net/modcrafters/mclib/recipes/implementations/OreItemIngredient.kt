package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.modcrafters.mclib.recipes.IItemIngredient
import net.ndrei.teslacorelib.utils.isEnough

class OreItemIngredient(val oreKey: String, val stackSize: Int): IItemIngredient {
    override fun isMatch(stack: ItemStack, ignoreSize: Boolean) =
        OreDictionary.getOres(this.oreKey).any {
            it.isEnough(stack, true) && (ignoreSize || (this.stackSize <= stack.count))
        }

    override val itemStacks: List<ItemStack>
        get() = OreDictionary.getOres(this.oreKey)
}
