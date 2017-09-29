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
    EMERALD(0x17DD62), // 33FF33);
    LAPIS(0x345EC3),
    REDSTONE(0xD50000),

//    COPPER(0xFF7F15),
//    TIN(0xC7ECFF),
//    SILVER(0xE5FCFF),
//    LEAD(0x8CA3E2),
//    ALUMINUM(0xFFB094),
//    NICKEL(0xE1E2C9),
//    PLATINUM(0x48ECFF),
//    IRIDIUM(0xD3D2E1),

    // Base Metals things:
    // https://github.com/MinecraftModDevelopmentMods/BaseMetals/blob/1.12/src/main/java/com/mcmoddev/basemetals/init/Materials.java
    ADAMANTINE(0x53393F),
    ANTIMONY(0xD8E3DE),
    AQUARIUM(0x000000),
    BISMUTH(0xDDD7CB),
    BRASS(0xFFE374),
    BRONZE(0xF7A54F),
    COLDIRON(0xC7CEF0),
    COPPER(0xFF9F78),
    CUPRONICKEL(0xC8AB6F),
    ELECTRUM(0xFFF2B3),
    INVAR(0xD2CDB8),
    LEAD(0x7B7B7B),
    MERCURY(0),
    MITHRIL(0xF4FFFF),
    NICKEL(0xEEFFEB),
    PEWTER(0x92969F),
    PLATINUM(0xF2FFFF),
    SILVER(0xFFFFFF),
    STARSTEEL(0x53393F),
    STEEL(0xD5E3E5),
    TIN(0xFFF7EE),
    ZINC(0xBCBCBC),

    // Modern Metals Things:
    // https://github.com/MinecraftModDevelopmentMods/ModernMetals/blob/master-1.12/src/main/java/com/mcmoddev/modernmetals/init/Materials.java
    ALUMINUM(0xC5C8C1),
    ALUMINUM_BRASS(0xEBAA56),
    CADMIUM(0xC9D4DA),
    CHROMIUM(0xCDCDCF),
    GALVANIZED_STEEL(0x9BA6A2),
    IRIDIUM(0xF8EDCC),
    MAGNESIUM(0x7F7F77),
    MANGANESE(0xF5CFDA),
    NICHROME(0xDEA054),
    OSMIUM(0x7C8E99),
    PLUTONIUM(0xB333EA),
    RUTILE(0xBF928B),
    STAINLESS_STEEL(0xC5BFC1),
    TANTALUM(0xC4BEC2),
    TITANIUM(0x73787E),
    TUNGSTEN(0x969696),
    URANIUM(0xA7B345),
    ZIRCONIUM(0x929793)

    ; companion object {
        @Suppress("unused")
        fun getColor(material: String): Int? =
                MaterialColors
                        .values()
                        .firstOrNull { it.name == material.toUpperCase() }
                        ?.color
    }
}
