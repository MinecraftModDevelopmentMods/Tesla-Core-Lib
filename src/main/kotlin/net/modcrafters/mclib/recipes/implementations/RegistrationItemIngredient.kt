package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class RegistrationItemIngredient(val modId: String, pathMask: String, val quantity: Int, val meta: Int) : BaseFilterItemIngredient(pathMask) {
    private val lazyItemStacks by lazy {
        Item.REGISTRY.keys.filter {
            this.isMatch(it.toString())
        }.map {
            ItemStack(Item.REGISTRY.getObject(it)!!, this.quantity, this.meta)
        }
    }

    override fun getStringsFor(stack: ItemStack) = listOf<String>(
        stack.item.registryName?.toString() ?: ""
    )

    override fun isMatch(info: String): Boolean {
        if (info.contains(':')) {
            val (modId, path) = info.indexOf(':').let {
                info.substring(0 .. (it - 1)) to info.substring(it + 1)
            }
            return (this.modId == modId) && super.isMatch(path)
        }
        return super.isMatch(info)
    }

    override val itemStacks: List<ItemStack>
        get() = this.lazyItemStacks
}
