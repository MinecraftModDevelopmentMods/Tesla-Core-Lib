package net.ndrei.teslacorelib.crafting

class OreDictIngredientEx(private val oreNames: Array<String>) : Ingredient(0) {
    override fun getMatchingStacks(): Array<ItemStack> =
        oreNames
            .mapNotNull { OreDictionary.getOres(it) }
            .fold(mutableListOf<ItemStack>()) { list, it -> list.also { _ -> list.addAll(it) } }
            .toTypedArray()
}
