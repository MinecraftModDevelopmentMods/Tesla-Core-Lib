package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.*

enum class CoreGearType(val material: String, val color: Int) {
    WOOD(MATERIAL_WOOD, MaterialColors.WOOD.color),
    STONE(MATERIAL_STONE, MaterialColors.STONE.color),
    IRON(MATERIAL_IRON, MaterialColors.IRON.color),
    LAPIS(MATERIAL_LAPIS, MaterialColors.LAPIS.color),
    REDSTONE(MATERIAL_REDSTONE, MaterialColors.REDSTONE.color),
    GOLD(MATERIAL_GOLD, MaterialColors.GOLD.color),
    EMERALD(MATERIAL_EMERALD, MaterialColors.EMERALD.color),
    DIAMOND(MATERIAL_DIAMOND, MaterialColors.DIAMOND.color)
}
