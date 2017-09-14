package net.ndrei.teslacorelib.tileentities

import net.minecraft.item.EnumDyeColor
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.TeslaEnergyLevelPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.inventory.SyncProviderLevel

/**
 * Created by CF on 2017-06-27.
 */
abstract class ElectricTileEntity protected constructor(typeId: Int) : SidedTileEntity(typeId) {
    protected lateinit var energyStorage: EnergyStorage
        private set

    //region inventory         methods

    override fun initializeInventories() {
        this.energyStorage = object : EnergyStorage(this.maxEnergy, this.energyInputRate, this.energyOutputRate) {
            override fun onChanged(old: Long, current: Long) {
//                this@ElectricTileEntity.markDirty()
//                this@ElectricTileEntity.forceSync()
                this@ElectricTileEntity.partialSync(SYNC_ENERGY)
            }
        }
        this.energyStorage.setSidedConfig(EnumDyeColor.LIGHT_BLUE, this.sideConfig, this.energyBoundingBox)
        this.registerSyncTagPart(SYNC_ENERGY, this.energyStorage, SyncProviderLevel.GUI)

        super.initializeInventories()
    }

    @Suppress("MemberVisibilityCanPrivate")
    protected val energyBoundingBox: BoundingRectangle
        get() = BoundingRectangle(7, 25, 18, 54)

    //endregion

    //region energy            methods

    protected open val maxEnergy: Long
        get() = 50000

    protected open val energyInputRate: Long
        get() = 80

    protected open val energyOutputRate: Long
        get() = 0

    //endregion

    //region storage & sync    methods

//    override fun readFromNBT(compound: NBTTagCompound) {
//        super.readFromNBT(compound)
//
////        if (compound.hasKey(SYNC_ENERGY)) {
////            this.energyStorage.deserializeNBT(compound.getCompoundTag(SYNC_ENERGY))
////        }
//    }
//
//    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
//        val nbt = super.writeToNBT(compound)
//
////        nbt.setTag(SYNC_ENERGY, this.energyStorage.serializeNBT())
//
//        return nbt
//    }

    //endregion

    //region capability & info methods

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        val oriented = this.orientFacing(facing)

        if (!this.isPaused()) {
            if (this.energyStorage.hasCapability(capability, oriented)) {
                return true
            }
        }

        return super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val oriented = this.orientFacing(facing)

        val c = this.energyStorage.getCapability(capability, oriented)
        if (c != null) {
            return c
        }

        return super.getCapability(capability, facing)
    }

    //endregion

    //#region gui / containers  nethods

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container)

        val energyBox = this.energyBoundingBox
        if (!energyBox.isEmpty) {
            pieces.add(TeslaEnergyLevelPiece(energyBox.left, energyBox.top, this.energyStorage))
        }

        return pieces
    }

    //#endregion

    public override final fun innerUpdate() {
        this.protectedUpdate()
        this.energyStorage.processStatistics()
    }

    protected abstract fun protectedUpdate()

    companion object {
        protected const val SYNC_ENERGY = "energy"
    }
}
