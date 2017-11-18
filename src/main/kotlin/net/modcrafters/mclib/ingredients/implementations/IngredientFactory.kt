package net.modcrafters.mclib.ingredients.implementations

import net.modcrafters.mclib.ingredients.IMachineIngredient

object IngredientFactory {
    fun getIngredientFromString(input: String): IMachineIngredient {
        val (modId, start) = if (input.contains(':'))
            input.indexOf(':').let { input.substring(0 until it) to (it + 1) }
        else "minecraft" to 0

        val (quantity, end) = if (input.contains('#'))
            input.lastIndexOf('#').let { input.substring(it + 1).toInt() to (it - 1) }
        else 1 to (input.length - 1)

        val middle = input.substring(start .. end)

        val (path, meta) = if (middle.contains('$'))
            middle.lastIndexOf('$').let { middle.substring(0 until it) to middle.substring(it + 1).toInt() }
        else middle to 0

        return when (modId) {
            "__ore" -> OreItemIngredient(path, quantity)
            "__class" -> ClassItemIngredient(path, quantity, meta)
            else -> RegistrationItemIngredient(modId, path, quantity, meta)
        }
    }
}
