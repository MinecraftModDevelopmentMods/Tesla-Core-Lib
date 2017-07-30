package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class SelfRenderingInventoryBakedModel(renderer: ISelfRenderingBlock, format: VertexFormat, val currentStack: ItemStack = ItemStack.EMPTY)
    : SelfRenderingBakedModel(renderer, format) {
    override val itemStack: ItemStack?
        get() = this.currentStack

    init {
        super.setTransform(ItemCameraTransforms.TransformType.GUI, super.getTransform(-15f, -3.5f, 0f, 30f, 225f, 0f, 0.625f))
        super.setTransform(ItemCameraTransforms.TransformType.GROUND, super.getTransform(-6f, -4f, -6f, 0f, 0f, 0f, 0.25f))
        super.setTransform(ItemCameraTransforms.TransformType.FIXED, super.getTransform(0f, 0f, 0f, 0f, 0f, 0f, 0.5f))
        super.addThirdPersonTransform(super.getTransform(-4f, -4f, -6f, 75f, 45f, 0f, 0.375f))
        super.addFirstPersonTransform(super.getTransform(-6f, -4f, -6f, 0f, 45f, 0f, 0.4f))
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true

    override fun getOverrides(): ItemOverrideList {
        return object: ItemOverrideList(mutableListOf()) {
            override fun handleItemState(originalModel: IBakedModel?, stack: ItemStack?, world: World?, entity: EntityLivingBase?): IBakedModel {
                if ((originalModel is SelfRenderingInventoryBakedModel) && (stack != null) && !stack.isEmpty) {
                    return SelfRenderingInventoryBakedModel(originalModel.renderer, originalModel.format, stack)
                }
                return super.handleItemState(originalModel, stack, world, entity)
            }
        }
    }
}