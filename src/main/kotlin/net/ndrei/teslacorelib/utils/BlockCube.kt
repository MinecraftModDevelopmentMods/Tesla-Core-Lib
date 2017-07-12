package net.ndrei.teslacorelib.utils

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by CF on 2017-07-06.
 */
class BlockCube internal constructor(pos1: BlockPos, pos2: BlockPos) : Iterable<BlockPos> {
    // the x-axis indicates the player's distance east (positive) or west (negative) of the origin point
    // the z-axis indicates the player's distance south (positive) or north (negative) of the origin point
    // the y-axis indicates how high or low (from 0 to 255, with 64 being sea level)

    val southEast = BlockPos(
            Math.max(pos1.x, pos2.x),
            Math.max(pos1.y, pos2.y),
            Math.max(pos1.z, pos2.z))
    val northWest = BlockPos(
            Math.min(pos1.x, pos2.x),
            Math.min(pos1.y, pos2.y),
            Math.min(pos1.z, pos2.z))

    fun getRandomInside(rnd: Random?): BlockPos {
        val x = this.southEast.x - this.northWest.x
        val y = this.southEast.y - this.northWest.y
        val z = this.southEast.z - this.northWest.z

        val rand = rnd ?: Random()
        return BlockPos(
                this.northWest.x + Math.round(rand.nextFloat() * x),
                this.northWest.y + Math.round(rand.nextFloat() * y),
                this.northWest.z + Math.round(rand.nextFloat() * z)
        )
    }

    val boundingBox: AxisAlignedBB
        get() = AxisAlignedBB(
                this.northWest.x.toDouble(),
                this.northWest.y.toDouble(),
                this.northWest.z.toDouble(),
                (this.southEast.x + 1).toDouble(),
                (this.southEast.y + 1).toDouble(),
                (this.southEast.z + 1).toDouble())

    override fun iterator(): Iterator<BlockPos> =  BlockPosIterator(this)

    private inner class BlockPosIterator(cube: BlockCube) : Iterator<BlockPos> {
        private var position = 0
        private val minX: Int = cube.northWest.x
        private val minY: Int = cube.northWest.y
        private val minZ: Int = cube.northWest.z
        private val maxX: Int = cube.southEast.x
        private val maxY: Int = cube.southEast.y
        private val maxZ: Int = cube.southEast.z

        private val xSize: Int
        private val ySize: Int
        private val zSize: Int

        init {
            this.xSize = this.maxX - this.minX + 1
            this.ySize = this.maxY - this.minY + 1
            this.zSize = this.maxZ - this.minZ + 1
        }

        override fun hasNext()
                = position < this.xSize * this.ySize * this.zSize

        override fun next(): BlockPos {
            val plane = position % (this.xSize * this.zSize)
            val y = position / (this.xSize * this.zSize)
            val x = plane % this.zSize
            val z = plane / this.zSize
            position++
            return BlockPos(this.minX + x, this.minY + y, this.minZ + z)
        }
    }

    fun <T : Entity> findEntities(entityClass: Class<T>, world: World)
        = world.getEntitiesWithinAABB(entityClass, this.boundingBox)!!

    fun pickItemEntities(world: World): List<ItemStack> {
        val stacks = mutableListOf<ItemStack>()

        for (entity in this.findEntities(EntityItem::class.java, world)) {
            val stack = entity.item
            if (!stack.isEmpty) {
                stacks.add(stack)
            }
            world.removeEntity(entity)
        }
        return stacks.toList()
    }

    companion object {
        fun pickItemEntities(world: World, pos: BlockPos, radius: Int): List<ItemStack> {
            return BlockCube(pos.east(radius).south(radius).down(radius), pos.west(radius).north(radius).up(radius))
                    .pickItemEntities(world)
        }
    }
}