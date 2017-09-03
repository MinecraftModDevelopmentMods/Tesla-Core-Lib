# 1.0.7
- fixed Mekanism energy acceptor integration

# 1.0.8
- added more helper functions around fluid tanks (SidedTileEntity.addSimpleFluidTank)
- added 'output only' fluid tanks
- added ModConfigHandler class that supports loading extra recipes json files for various machines
- allow for better customization of BasicTeslaContainer, BasicTeslaGUIContainer and SidedTileEntity
- added SidedTileEntity.addSimpleInventory all-in-one inventory creation method
- added configurable mod-level boolean flags to AutoRegister Item/Block annotations
- added configuration options to disable registration of powders, sheets, gears, addons etc 
- added lapis gear / sheet
- added redstone gear / sheet
- new recipe ingredient factory: "teslacorelib:ore_dict_ex" - you can supply a comma separated list of ore dictionary keys in the "ore" field
- new recipe condition factory: "teslacorelib:ore_dict" - tests if an ore dict key has any items registered to it

#1.0.9