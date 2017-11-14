package net.modcrafters.mclib.machines

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.modcrafters.mclib.checkEmpty
import net.modcrafters.mclib.features.IFeature
import net.modcrafters.mclib.features.implementations.FeatureWrapper
import net.modcrafters.mclib.features.wrap
import net.modcrafters.mclib.getNullOrCompound
import net.modcrafters.mclib.setNonNullTag

abstract class FeaturesMachines: TileEntity(), IFeaturesMachine {
    private val _featuresMap = mutableMapOf<String, FeatureWrapper>()
    protected var featuresMap: Map<String, FeatureWrapper> private set
    override val features get() = this.featuresMap.values.toList()

    private var featuresInitialized = false
    private var worldCreateSet: World? = null
    private var worldSet = false
    private var posSet = false
    private val dirtyFeatures = mutableSetOf<String>()

    init {
        this.featuresMap = this._featuresMap.toMap()
    }

    //#region FEATURES

    override fun addFeature(feature: IFeature) {
        if (this.featuresMap.containsKey(feature.key))
            throw Exception("A feature with key '${feature.key}' already exists!")
        if (!feature.canBeAddedTo(this))
            throw Exception("Feature '${feature.key}' cannot be added to this machine!")
        this._featuresMap[feature.key] = feature.wrap(this)
        this.featuresMap = this._featuresMap.toMap()
        feature.added(this)
    }

    override fun removeFeature(key: String) {
        if (this.featuresMap.containsKey(key)) {
            val feature = this.featuresMap[key] ?: return // TODO: or throw error?
            this._featuresMap.remove(key)
            this.featuresMap = this._featuresMap.toMap()
            feature.removed(this)
        }
        // TODO: consider throwing an error if key not found
    }

    private fun initializeFeatures() {
        this.featuresInitialized = true
        this.initFeatures()
    }

    abstract fun initFeatures()

    override val machineWorld get() = this.getWorld() ?: this.worldCreateSet ?: throw Exception("World was not set yet!")
    override val machinePos get() = this.getPos() ?: throw Exception("Pos was not set yet!")

    override final fun featureChanged(feature: IFeature, makeDirty: Boolean) {
        @Suppress("UNNECESSARY_SAFE_CALL")
        if (this.getWorld()?.isRemote == false) {
            if (makeDirty) {
                this.markDirty()
            }
            this.handleFeatureChanged(feature.key)
        }
        this.onFeatureChanged(feature.key)
    }

    protected open fun onFeatureChanged(featureKey: String) {}

    protected open fun handleFeatureChanged(featureKey: String) {
        val state = this.getWorld().getBlockState(this.getPos())
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3)
    }

    private fun findDirtyFeatures() =
        this.dirtyFeatures
            .mapNotNull { key -> this.featuresMap[key] }

    private fun getFeaturesUpdatePacket() = this.findDirtyFeatures()
        .fold(NBTTagCompound()) { nbt, feature -> nbt.setNonNullTag(feature.key, feature.writeSyncNBT()) }
        .checkEmpty()

    override fun getUpdatePacket() = SPacketUpdateTileEntity(this.getPos(), 42, this.updateTag)

    //#endregion

    //#region SYNC & NBT

    //#region world/pos set wrappers

    override fun setWorld(worldIn: World) {
        this.worldSet = true
        super.setWorld(worldIn)
        this.worldCreateSet = null
        if (this.posSet && !this.featuresInitialized) {
            this.initializeFeatures()
        }
    }

    override fun setPos(posIn: BlockPos) {
        this.posSet = true
        super.setPos(posIn)
        if (this.worldSet && !this.featuresInitialized) {
            this.initializeFeatures()
        }
    }

    override fun setWorldCreate(worldIn: World) {
        this.worldCreateSet = worldIn
        super.setWorldCreate(worldIn)
    }

    //#endregion

    override fun readFromNBT(compound: NBTTagCompound) {
        if ((this.worldCreateSet != null) && !this.featuresInitialized) {
            // this will be set again in super... but... that's life
            this.pos = BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"))
            this.initializeFeatures()
            this.worldCreateSet = null
        }
        super.readFromNBT(compound)
        this.readFeaturesFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        this.writeFeaturesToNBT()
        return super.writeToNBT(compound)
    }

    private fun writeFeaturesToNBT() {
        super.getTileData().setNonNullTag(FEATURES_NBT_KEY, this.serializeFeaturesNBT())
    }

    @Deprecated("Remove once 1.13 arrives.", ReplaceWith("refreshFeaturesFromNBT"))
    private fun readFeaturesFromNBT(nbt: NBTTagCompound) {
        val featuresNBT = this.tileData.getNullOrCompound(FEATURES_NBT_KEY)
        this.features.forEach { feature ->
            feature.deserializeNBT(featuresNBT?.getNullOrCompound(feature.key) ?: nbt.getNullOrCompound(feature.key))
        }

        // TODO: Once 1.13 arrives replace this method with just this:
        // this.deserializeFeaturesNBT(this.tileData.getNullOrCompound(FEATURES_NBT_KEY))
    }

    //#endregion

    companion object {
        const val FEATURES_NBT_KEY = "features"
        const val SYNC_PACKAGE_KEY = "_sync_"
    }
}
