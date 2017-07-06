package net.ndrei.teslacorelib.inventory

import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider
import net.ndrei.teslacorelib.gui.*

/**
 * Created by CF on 2017-06-28.
 */
open class ColoredItemHandler(handler: IItemHandler, val color: EnumDyeColor, val name: String, val index: Int?, val boundingBox: BoundingRectangle)
    : FilteredItemHandler(handler), IContainerSlotsProvider, IGuiContainerPiecesProvider {

    constructor(handler: IItemHandler, color: EnumDyeColor, name: String, boundingBox: BoundingRectangle)
        : this(handler, color, name, null, boundingBox)

    private var containerItemHandler: IFilteredItemHandler? = null

    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
        val result = mutableListOf<Slot>()

        val box = this.boundingBox
        if (!box.isEmpty) {
            val columns = box.width / 18
            (0..this.innerHandler.slots - 1).mapTo(result) {
                FilteredSlot(this.itemHandlerForContainer, it,
                        box.left + 1 + (it % columns) * 18,
                        box.top  + 1 + (it / columns) * 18
                )
            }
        }

        return result
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val result = mutableListOf<IGuiContainerPiece>()

        val box = this.boundingBox
        if ((!box.isEmpty) && (container.teslaContainer != null)) {
            this.getSlots(container.teslaContainer!!).forEachIndexed { index, it ->
                result.add(TiledRenderedGuiPiece(it.xPos - 1, it.yPos - 1,
                        18, 18,1, 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, this.color))

                if (this.innerHandler is LockableItemHandler) {
                    result.add(object : BasicContainerGuiPiece(it.xPos, it.yPos, 18, 18) {
                        override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
                            container.drawTexturedModalRect(
                                    guiX + this.left,
                                    guiY + this.top,
                                    if (this@ColoredItemHandler.innerHandler.locked) 163 else 181, 191, 16, 16)
                        }
                    })

                    result.add(
                        object : GhostedItemStackRenderPiece(box.left, box.top) {
                            override val isVisible: Boolean
                                get() = this@ColoredItemHandler.innerHandler.getStackInSlot(index).isEmpty

                            override fun getRenderStack()
                                    = this@ColoredItemHandler.innerHandler.getFilterStack(index)
                        })
                }
            }
        }

        return result
    }

    protected val itemHandlerForContainer: IFilteredItemHandler
        get() {
            if (this.containerItemHandler == null) {
                this.containerItemHandler = object : FilteredItemHandler(this.innerHandler) {
                    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                        return this@ColoredItemHandler.canInsertItem(slot, stack)
                    }
                }
            }
            return this.containerItemHandler!!
        }
}
