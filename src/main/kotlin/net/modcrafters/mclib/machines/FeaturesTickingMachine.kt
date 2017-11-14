package net.modcrafters.mclib.machines

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.ITickable
import net.minecraft.world.WorldServer
import net.modcrafters.mclib.checkEmpty
import net.modcrafters.mclib.setNonNullTag
import net.modcrafters.mclib.wrapInTag

abstract class FeaturesTickingMachine: FeaturesMachines(), ITickable {
    private val dirtyFeatures = mutableSetOf<String>()

    //#region SYNC

    override final fun handleFeatureChanged(featureKey: String) {
        this.dirtyFeatures.add(featureKey)
    }

    private fun findDirtyFeatures() =
        this.dirtyFeatures
            .mapNotNull { key -> this.featuresMap[key] }

    private fun getFeaturesUpdatePacket() = this.findDirtyFeatures()
        .fold(NBTTagCompound()) { nbt, feature -> nbt.setNonNullTag(feature.key, feature.writeSyncNBT()) }
        .checkEmpty()

    private fun sendSyncPacket(): Boolean {
        val world = this.getWorld()
        if (this.dirtyFeatures.isNotEmpty() && (world is WorldServer)) {
            val nbt = getFeaturesUpdatePacket()
            this.dirtyFeatures.clear()
            if (nbt != null) {
                val pos = this.getWorld().getChunkFromBlockCoords(this.getPos()).pos
                val entry = world.playerChunkMap.getEntry(pos.x, pos.z)
                entry?.sendPacket(SPacketUpdateTileEntity(this.pos, 42,  nbt.wrapInTag(SYNC_PACKAGE_KEY)))
                return true
            }
        }
        return false
    }

    //#endregion

    override final fun update() {
        this.innerUpdate()
        this.sendSyncPacket()
    }

    protected fun innerUpdate() {}
}
