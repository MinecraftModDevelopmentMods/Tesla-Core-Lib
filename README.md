# Tesla-Core-Lib
[![](http://cf.way2muchnoise.eu/tesla-core-lib.svg)](https://minecraft.curseforge.com/projects/tesla-core-lib)
[![](http://cf.way2muchnoise.eu/versions/tesla-core-lib.svg)](https://minecraft.curseforge.com/projects/tesla-core-lib)
[![](https://img.shields.io/badge/Discord-MMD%20Cat%20Mods-blue.svg)](https://discord.gg/xDw3Vkj)

Minecraft library useful in creating power hungry machines

### If one wants to use this lib in his mod
One must first add the maven repository to the list:
```gradle
repositories {
    maven { name='TCL'; url='https://maven.mcmoddev.com' }
}
``` 
And then one must add the dependency:
```gradle
compile("net.ndrei:tesla-core-lib:${project.teslacorelib_mc_version}-${project.teslacorelib_version}:deobf") { changing = true }
```
And I would also recommend having these in your `gradle.properties`:
```gradle
teslacorelib_mc_version = 1.12
teslacorelib_version = 1.0.13
```
For easier update later on.

The artifacts for the latest version will get updated on maven after each git push.

The version number goes up after every release on curse forge. 