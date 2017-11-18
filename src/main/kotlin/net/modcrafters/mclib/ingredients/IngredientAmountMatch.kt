package net.modcrafters.mclib.ingredients

enum class IngredientAmountMatch(private val comparer: (Long, Long) -> Boolean) {
    EXACT({ a, b -> (a == b) }),
    IGNORE_SIZE({ _, _ -> true }),
    BE_ENOUGH({ a, b -> (a <= b) });

    fun compare(source: Int, target: Int) = this.comparer(source.toLong(), target.toLong())
    fun compare(source: Long, target: Long) = this.comparer(source, target)
}
