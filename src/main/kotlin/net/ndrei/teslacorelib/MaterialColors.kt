package net.ndrei.teslacorelib

/**
 * Created by CF on 2017-07-05.
 */
enum class MaterialColors(val color: Int) {
    WOOD(0x86592D),
    STONE(0x808080),

    IRON(0xD8AF93), // FFFCFC),
    GOLD(0xFCEE4B), // FFD700),
    COAL(0x3F3F3F), // 333344),
    DIAMOND(0x5DECF5), // 4DFFFF),
    EMERALD(0x17DD62); // 33FF33);

    companion object {
        @Suppress("unused")
        fun getColor(material: String): Int? =
                MaterialColors
                        .values()
                        .firstOrNull { it.name == material.toUpperCase() }
                        ?.color
    }
}
