package net.ndrei.teslacorelib.tileentities

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.inventory.SyncProviderLevel
import net.ndrei.teslacorelib.netsync.ISimpleNBTMessageHandler
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import java.util.function.Consumer
import java.util.function.Supplier

abstract class SyncTileEntity(private val entityTypeId: Int = 0): TileEntity(), ITickable, ISimpleNBTMessageHandler {
    private var syncTick = SyncTileEntity.SYNC_ON_TICK

    private val syncKeys = mutableSetOf<String>()
    private val syncParts = mutableMapOf<String, SyncTileEntity.SyncPartInfo>()

    private var containerRefCount = 0

    @Suppress("UNUSED_PARAMETER")
    fun containerOpened(container: BasicTeslaContainer<*>, player: EntityPlayerMP) {
        this.containerRefCount++
        this.forceSync() // force full sync
        this.testSync() // sync now, don't wait for next tick
    }

    @Suppress("UNUSED_PARAMETER")
    fun containerClosed(container: BasicTeslaContainer<*>, player: EntityPlayerMP) {
        this.containerRefCount--
    }

    private class SyncPartInfo(val level: SyncProviderLevel, val nbtType: Int, val reader: (NBTBase) -> Unit, val writer: () -> NBTBase?)

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
//        if ((this.getWorld() != null) && this.getWorld().isRemote) {
//            TeslaCoreLib.logger.info("Full Sync received for: ${this.javaClass.name} at ${this.pos}.")
//        }
        compound.readSyncParts()
    }

    private fun NBTTagCompound.readSyncParts() {
        this@SyncTileEntity.syncParts.forEach { key, part ->
            if (this.hasKey(key, part.nbtType)) {
                part.reader(this.getTag(key))

                @Suppress("UNNECESSARY_SAFE_CALL")
                if (this@SyncTileEntity.getWorld()?.isRemote == true) {
                    this@SyncTileEntity.onSyncPartSynced(key)
                }
            }
        }
    }

    protected fun writeToNBT(): NBTTagCompound =
        this.setupSpecialNBTMessage().also { this.writeToNBT(it) }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound =
        super.writeToNBT(compound).writeSyncParts(this.syncParts.filter {
            it.value.level.shouldSync(false, false, true)
        })

    private fun writePartialNBT(keys: Set<String>): NBTTagCompound =
        this.setupSpecialNBTMessage("PARTIAL_SYNC").writeSyncParts(this.syncParts.filter {
            keys.contains(it.key) && it.value.level.shouldSync(this.containerRefCount > 0, true, false)
        })

    private fun NBTTagCompound.writeSyncParts(parts: Map<String, SyncPartInfo>) =
        this.also { nbt ->
            parts.forEach { key, part ->
                val tag = part.writer()
                if (tag != null) {
                    nbt.setTag(key, part.writer())
                }
            }
        }

    override fun getUpdateTag(): NBTTagCompound = super.getUpdateTag().also {
        this.writeToNBT(it)
    }

    fun setupSpecialNBTMessage(messageType: String? = null): NBTTagCompound {
        val compound = NBTTagCompound()
        compound.setInteger("__tetId", this.entityTypeId)
        if ((messageType != null) && messageType.isNotEmpty()) {
            compound.setString("__messageType", messageType)
        }
        return compound
    }

    protected fun forceSync() {
        if (this.getWorld() != null && !this.getWorld().isRemote) {
            this.syncTick = SyncTileEntity.SYNC_ON_TICK
        }
    }

    protected fun partialSync(key: String, markDirty: Boolean = true) {
        if ((this.getWorld()?.isRemote == false) && key.isNotEmpty()) {
            this.syncKeys.add(key)
        }

        if (markDirty) {
            //copied from super method, avoid sending neighbor updates (they are too expensive and not needed)
            if (this.getWorld() != null) {
                this.getWorld().markChunkDirty(this.pos, this)
            }
        }

        this.onSyncPartUpdated(key)
    }

    protected open fun onSyncPartUpdated(key: String) {}

    @SideOnly(Side.CLIENT)
    protected open fun onSyncPartSynced(key: String) {}

    override final fun handleServerMessage(message: SimpleNBTMessage): SimpleNBTMessage? {
        val compound = message.compound
        if (compound != null) {
            val tetId = compound.getInteger("__tetId")
            if (tetId == this.entityTypeId) {
                if (compound.hasKey("__messageType", Constants.NBT.TAG_STRING)) {
                    val messageType = compound.getString("__messageType")
                    return this.processServerMessage(messageType, compound)
                } else /*if (this.getWorld().isRemote)*/ {
                    this.processServerMessage(compound)
                }
            } else {
                TeslaCoreLib.logger.info("Unknown message for __tetId: " + tetId + " : " + compound.toString())
            }
        }
        return null
    }

    override final fun handleClientMessage(player: EntityPlayerMP?, message: SimpleNBTMessage): SimpleNBTMessage? {
        val compound = message.compound
        if (compound != null) {
            val tetId = compound.getInteger("__tetId")
            if (tetId == this.entityTypeId) {
                if (compound.hasKey("__messageType", Constants.NBT.TAG_STRING)) {
                    val messageType = compound.getString("__messageType")
                    return this.processClientMessage(messageType, player, compound)
                }
            } else {
                TeslaCoreLib.logger.info("Unknown message for __tetId: " + tetId + " : " + compound.toString())
            }
        }
        return null
    }

    protected fun processServerMessage(compound: NBTTagCompound) {
        this.readFromNBT(compound)
    }

    private fun testSync() {
        if (this.syncTick >= SYNC_ON_TICK) {
//            TeslaCoreLib.logger.info("Full TileEntity Sync at: ${this.pos}")
            TeslaCoreLib.network.send(SimpleNBTMessage(this, this.writeToNBT()))
            this.syncTick = 0
            this.syncKeys.clear() // don't care about partial sync anymore
        }

        if (this.syncKeys.count() > 0) {
            val set = this.syncKeys
            val nbt = this.writePartialNBT(set)
            if (nbt.keySet.any { !it.startsWith("__") }) {
//                TeslaCoreLib.logger.info("Partial TileEntity Sync [${set.joinToString(", ")}] at: ${this.pos}")
                TeslaCoreLib.network.send(SimpleNBTMessage(this, nbt))
            }
            this.syncKeys.clear()
        }
    }

    protected open fun processServerMessage(messageType: String, compound: NBTTagCompound): SimpleNBTMessage? {
        if (messageType == "PARTIAL_SYNC") {
//            TeslaCoreLib.logger.info("Partial Sync [${compound.keySet.joinToString(", ")}] received for: ${this.javaClass.name} at ${this.pos}.")
            compound.readSyncParts()
        }
        return null
    }
    
    protected open fun processClientMessage(messageType: String?, player: EntityPlayerMP?, compound: NBTTagCompound): SimpleNBTMessage? =
        this.processClientMessage(messageType, compound)


    protected open fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? = null

    fun sendToServer(compound: NBTTagCompound) {
        TeslaCoreLib.network.sendToServer(SimpleNBTMessage(this, compound))
    }

    private inline fun <reified TT : NBTBase> Consumer<TT>.makeReader(key: String? = null): (NBTBase) -> Unit = { it ->
        if (it is TT) {
            this.accept(it)
        } else if (!key.isNullOrBlank()) {
            TeslaCoreLib.logger.warn("Wrong sync message received for '$key'. Expected '${TT::class.java.name}' but found '${it.javaClass.name}'.")
        }
    }

    private inline fun <reified TT : NBTBase> Supplier<TT?>.makeWriter(): () -> NBTBase? = { this.get() }

    protected fun registerSyncPart(key: String, nbtType: Int, reader: (NBTBase) -> Unit, writer: () -> NBTBase?, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.syncParts.putIfAbsent(key, SyncPartInfo(level, nbtType, reader, writer))
    }

    protected fun registerSyncIntPart(key: String, reader: Consumer<NBTTagInt>, writer: Supplier<NBTTagInt?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_INT, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncBytePart(key: String, reader: Consumer<NBTTagByte>, writer: Supplier<NBTTagByte?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_BYTE, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncStringPart(key: String, reader: Consumer<NBTTagString>, writer: Supplier<NBTTagString?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_STRING, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncFloatPart(key: String, reader: Consumer<NBTTagFloat>, writer: Supplier<NBTTagFloat?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_FLOAT, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncTagPart(key: String, reader: Consumer<NBTTagCompound>, writer: Supplier<NBTTagCompound?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_COMPOUND, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncTagPart(key: String, thing: INBTSerializable<NBTTagCompound>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_COMPOUND,
            (Consumer<NBTTagCompound> { thing.deserializeNBT(it) }).makeReader(key),
            (Supplier<NBTTagCompound?> { thing.serializeNBT() }).makeWriter(),
            level)
    }

    protected fun registerSyncListPart(key: String, reader: Consumer<NBTTagList>, writer: Supplier<NBTTagList?>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_LIST, reader.makeReader(key), writer.makeWriter(), level)
    }

    protected fun registerSyncListPart(key: String, thing: INBTSerializable<NBTTagList>, level: SyncProviderLevel = SyncProviderLevel.TICK) {
        this.registerSyncPart(key, Constants.NBT.TAG_LIST,
            (Consumer<NBTTagList> { thing.deserializeNBT(it) }).makeReader(key),
            (Supplier<NBTTagList?> { thing.serializeNBT() }).makeWriter(),
            level)
    }

    protected fun notifyNeighbours() {
        if (this.getWorld() != null) {
            this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true)
        }
    }

    override fun update() {
        if (!this.getWorld().isRemote) {
            // this.syncTick++ // <-- hopefully this will reduce lag and not create additional problems
            this.testSync()
        }
    }

    companion object {
        private const val SYNC_ON_TICK = 20
    }
}