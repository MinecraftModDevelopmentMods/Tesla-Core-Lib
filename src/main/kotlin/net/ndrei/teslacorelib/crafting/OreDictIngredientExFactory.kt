package net.ndrei.teslacorelib.crafting

class OreDictIngredientExFactory : IIngredientFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient {
        val oreNames = JsonUtils.getString(json, "ore", "")
            .split(',')
            .mapNotNull { it.trim().let { if (it.isBlank()) null else it } }
            .toTypedArray()
        return if (oreNames.isNotEmpty()) {
            OreDictIngredientEx(oreNames)
        }
        else
            throw JsonSyntaxException("No ore names found.")
    }
}
