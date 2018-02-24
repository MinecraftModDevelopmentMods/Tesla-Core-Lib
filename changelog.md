#future (1.13)
- rework how fluid tanks are handled, make similar to item handlers
- change mod name / id / packages to 'mclib' (Mod Crafters Lib)
- remove 'tesla' from all the names

#future
- update forge version
- add java doc stuff
- add a wiki
- add 'recipe' framework + base 'recipe' machine
- add config annotations for config files + building config gui
- seal capability methods on tile entities and make "better" open ones
- look at this method for sending sync packets to client:
<pre>
ChunkPos cp = this.world.getChunkFromBlockCoords(getPos()).getPos();
PlayerChunkMapEntry entry = ((WorldServer)this.world).getPlayerChunkMap().getEntry(cp.x, cp.z);
if (entry != null) {
    entry.sendPacket(this.getUpdatePacket());
}
</pre>

#1.0.13
- fixed problem with conflict between 'allowMachinesToSpawnItems' and tesla wrench picking up tile entities
- added pt_BR translation
- fixed issue #31: https://github.com/MinecraftModDevelopmentMods/Tesla-Core-Lib/issues/31
- moved to MMD servers
- jar should now be signed

#1.0.12
- updated forgelin
- added customizable energy display mode for energy storage gui pieces

#1.0.11
- added generic config gui handler
- improve config file handling
- added support for addons that provide/override tile entity capabilities
- added new SyncTileEntity base class that handles all of the sync magic between server and client

#1.0.10
- improved self baked block support
- added IGuiTexture and IGuiIcon helper interfaces
- added GuiTexture and GuiIcon helper classes
- added 'isEnabled' member to ButtonPiece and ToggleButtonPiece
- new textures for powders and gears

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

