#future (1.13)
- rework how fluid tanks are handled, make similar to item handlers

#future
- add java doc stuff
- add a wiki
- update forge version

#1.0.10

#1.0.9
- completely changed how tile entities sync stuff from server to client (should be like 99.9% less network traffic now)
- localize all the things!

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

# 1.0.7
- fixed Mekanism energy acceptor integration

