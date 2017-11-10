package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.blocks.multipart.IBlockPart
import net.ndrei.teslacorelib.blocks.multipart.IBlockPartProvider
import net.ndrei.teslacorelib.blocks.multipart.MultiPartRayTraceResult
import net.ndrei.teslacorelib.utils.getHeldItem

abstract class MultiPartBlock(modId: String, tab: CreativeTabs?, registryName: String, material: Material)
    : RegisteredBlock(modId, tab, registryName, material) {

    protected open fun getParts(world: World, pos: BlockPos): List<IBlockPart> {
        val te = world.getTileEntity(pos)
        if (te is IBlockPartProvider) { // TODO: maybe make this a capability?
            return te.getParts()
        }
        return listOf()
    }

    override fun isFullCube(state: IBlockState?) = false
    override fun isFullBlock(state: IBlockState?) = false

    override fun getUseNeighborBrightness(state: IBlockState?) = true

    override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = false
    override fun shouldSideBeRendered(blockState: IBlockState?, blockAccess: IBlockAccess?, pos: BlockPos?, side: EnumFacing?) = true

    //#region RAY TRACE

    protected open fun transformCollisionAABB(aabb: AxisAlignedBB, state: IBlockState): AxisAlignedBB = aabb

    private fun AxisAlignedBB.transform(state: IBlockState) = this@MultiPartBlock.transformCollisionAABB(this, state)

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
        this.getParts(worldIn, pos).forEach {
            if (it.canBeHitWith(worldIn, pos, state, null, ItemStack.EMPTY)) {
                it.hitBoxes.forEach {
                    Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, it.aabb.transform(state))
                }
            }
        }
    }

    fun rayTrace(world: World, pos: BlockPos, player: EntityPlayer, stack: ItemStack): RayTraceResult? {
        val start = player.positionVector.addVector(0.0, player.getEyeHeight().toDouble(), 0.0)
        var reachDistance = 5.0
        if (player is EntityPlayerMP) {
            reachDistance = player.interactionManager.blockReachDistance
        }
        val end = start.add(player.lookVec.normalize().scale(reachDistance))
        return this.rayTrace(world, pos, start, end, player, stack)
    }

    override fun collisionRayTrace(state: IBlockState, world: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
        val player = Minecraft.getMinecraft().player
        return this.rayTrace(world, pos, start, end, player, player.getHeldItem())
    }

    fun rayTrace(world: World, pos: BlockPos, start: Vec3d, end: Vec3d, player: EntityPlayer?, stack: ItemStack) =
        this.getParts(world, pos)
            .filter { it.canBeHitWith(world, pos, world.getBlockState(pos), player, stack) }
            .fold<IBlockPart, RayTraceResult?>(null) { b1, part ->
                part.hitBoxes.fold(b1) { b2, hitBox ->
                    this.computeTrace(b2, pos, start, end, hitBox.aabb.transform(world.getBlockState(pos)), MultiPartRayTraceResult(part, hitBox))
                }
            }

    private fun computeTrace(lastBest: RayTraceResult?, pos: BlockPos, start: Vec3d, end: Vec3d,
                             aabb: AxisAlignedBB, info: MultiPartRayTraceResult?): RayTraceResult? {
        val next = super.rayTrace(pos, start, end, aabb) ?: return lastBest
        next.subHit = if (info == null) -1 else 42
        next.hitInfo = info
        if (lastBest == null) {
            return next
        }
        val distLast = lastBest.hitVec.squareDistanceTo(start)
        val distNext = next.hitVec.squareDistanceTo(start)
        return if (distLast > distNext) next else lastBest
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
//        val player = Minecraft.getMinecraft().player
//        val trace = rayTrace(world, pos, player, player.getHeldItem())
        val trace = Minecraft.getMinecraft().objectMouseOver
        if (trace == null || trace.subHit < 0 || pos != trace.blockPos) {
            return FULL_BLOCK_AABB
        }
        val mainId = trace.subHit
        val info = trace.hitInfo as? MultiPartRayTraceResult
        val aabb = if ((mainId == 1) && (info != null)) {
            info.hitBox.aabb.transform(state)
        } else FULL_BLOCK_AABB
        return aabb.grow(1 / 32.0).offset(pos)
    }

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if ((worldIn != null) && (pos != null) && (playerIn != null)) {
            val trace = rayTrace(worldIn, pos, playerIn, playerIn.getHeldItem())
            if (trace != null) {
                val info = if (trace.subHit == 42) trace.hitInfo as? MultiPartRayTraceResult else null
                if (info != null) {
                    val te = worldIn.getTileEntity(pos)
                    if (te is IBlockPartProvider) { // TODO: maybe make this a capability?
                        return te.onPartActivated(playerIn, hand ?: EnumHand.MAIN_HAND, info.part, info.hitBox)
                    }
                }
            }
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
    }

    //#endregion
}
