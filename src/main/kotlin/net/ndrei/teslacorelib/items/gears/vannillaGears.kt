package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.*
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_WOOD}")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_WOOD}")
object GearWoodItem : ColoredGearItem(CoreGearType.WOOD)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_STONE}")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_STONE}")
object GearStoneItem : ColoredGearItem(CoreGearType.STONE)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_IRON}")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_IRON}")
object GearIronItem : ColoredGearItem(CoreGearType.IRON)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_REDSTONE}")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_REDSTONE}")
object GearRedstoneItem : ColoredGearItem(CoreGearType.REDSTONE)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#$MATERIAL_LAPIS")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_LAPIS}")
object GearLapisItem : ColoredGearItem(CoreGearType.LAPIS)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#$MATERIAL_GOLD")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_GOLD}")
object GearGoldItem : ColoredGearItem(CoreGearType.GOLD)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#$MATERIAL_EMERALD")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_EMERALD}")
object GearEmeraldItem : ColoredGearItem(CoreGearType.EMERALD)

@AutoRegisterGear("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#$MATERIAL_DIAMOND")
@AutoRegisterColoredThingy("${TeslaCoreLibConfig.REGISTER_GEAR_TYPES}#${MATERIAL_DIAMOND}")
object GearDiamondItem : ColoredGearItem(CoreGearType.DIAMOND)
