# Tesla-Core-Lib
[![](http://cf.way2muchnoise.eu/tesla-core-lib.svg)](https://minecraft.curseforge.com/projects/tesla-core-lib)
[![](http://cf.way2muchnoise.eu/versions/tesla-core-lib.svg)](https://minecraft.curseforge.com/projects/tesla-core-lib)
[![codebeat badge](https://codebeat.co/badges/1bab3be1-4830-4dea-88c8-c987dc536d17)](https://codebeat.co/projects/github-com-faceofcat-tesla-core-lib-1-12)
[![](https://img.shields.io/badge/Discord-Mod%20Crafters-blue.svg)](https://discord.gg/wmseqAS)

Minecraft library useful in creating power hungry machines

### If one wants to use this lib in his mod
One must first add the maven repository to the list:
```gradle
repositories {
    maven { name='TCL'; url='https://maven.modcrafters.net' }
}
``` 
And then one must add the dependency:
```gradle
compile("net.ndrei:tesla-core-lib:${project.teslacorelib_mc_version}-${project.teslacorelib_version}:deobf") { changing = true }
```
And I would also recommend having these in your `gradle.properties`:
```gradle
teslacorelib_mc_version = 1.12
teslacorelib_version = 1.0.12
```
For easier update later on.

The artifacts for the latest version will get updated on maven after each git push.

The version number goes up after every release on curse forge. 