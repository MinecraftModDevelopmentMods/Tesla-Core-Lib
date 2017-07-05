package net.ndrei.teslacorelib.tileentities

import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.RedstoneTogglePiece
import net.ndrei.teslacorelib.gui.TeslaEnergyLevelPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.inventory.IRedstoneControlledMachine
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage

/**
 * Created by CF on 2017-06-27.
 */
abstract class ElectricTileEntity protected constructor(typeId: Int) : SidedTileEntity(typeId), IRedstoneControlledMachine {
    private var redstoneSetting = IRedstoneControlledMachine.RedstoneControl.AlwaysActive

    protected lateinit var energyStorage: EnergyStorage
        private set

    //region inventory         methods

    override fun initializeInventories() {
        this.energyStorage = object : EnergyStorage(this.maxEnergy, this.energyInputRate, this.energyOutputRate) {
            override fun onChanged() {
                this@ElectricTileEntity.markDirty()
                this@ElectricTileEntity.forceSync()
            }
        }
        this.energyStorage.setSidedConfig(EnumDyeColor.LIGHT_BLUE, this.sideConfig, this.energyBoundingBox)

        super.initializeInventories()
    }

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

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("energy")) {
            this.energyStorage!!.deserializeNBT(compound.getCompoundTag("energy"))
        }

        if (compound.hasKey("redstone", Constants.NBT.TAG_STRING)) {
            this.redstoneSetting = IRedstoneControlledMachine.RedstoneControl.valueOf(compound.getString("redstone"))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        compound.setTag("energy", this.energyStorage!!.serializeNBT())

        compound.setString("redstone", this.redstoneSetting.name)

        return compound
    }

    override fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? =
            when (messageType) {
                "REDSTONE_CONTROL" -> {
                    this.redstoneSetting = IRedstoneControlledMachine.RedstoneControl.valueOf(compound.getString("setting"))
                    null
                }
                else -> super.processClientMessage(messageType, compound)
            }

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

        if (this.allowRedstoneControl) {
            pieces.add(RedstoneTogglePiece(this))
        }

        return pieces
    }

    //#endregion

    //#region redstone control  members

    override val allowRedstoneControl: Boolean
        get() = true
    override final val redstoneControl: IRedstoneControlledMachine.RedstoneControl
        get() = this.redstoneSetting

    override final fun toggleRedstoneControl() {
        val new = this.redstoneSetting.getNext()
        if (TeslaCoreLib.isClientSide) {
            val message = this.setupSpecialNBTMessage("REDSTONE_CONTROL")
            message.setString("setting", new.name)
            super.sendToServer(message)
        }
        this.redstoneSetting = new
    }

    //#endregion

    public override fun innerUpdate() {
        if (this.allowRedstoneControl && this.redstoneSetting.canRun { this.world.getRedstonePower(this.pos, this.facing) }) {
            this.protectedUpdate()
        }

        this.energyStorage.processStatistics()
    }

    protected abstract fun protectedUpdate()
}
