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
        if (!box.isEmpty) {
            result.add(TiledRenderedGuiPiece(box.left, box.top, 18, 18,
                    box.width / 18, box.height / 18,
                    BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, this.color))

            if (this.innerHandler is LockableItemHandler) {
                result.add(object: BasicContainerGuiPiece(box.left, box.top, box.width, box.height) {
                    override fun drawMiddleLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
                        val handler = this@ColoredItemHandler.innerHandler
                        val textureX = if (handler.locked) 163 else 181
                        for (x in 0..this.width / 18 - 1) {
                            for (y in 0..this.height / 18 - 1) {
                                container.drawTexturedModalRect(
                                        guiX + this.left + 1 + x * 18,
                                        guiY + this.top + 1 + y * 18,
                                        textureX, 191, 16, 16)
                            }
                        }
                    }
                })

                val columns = box.width / 18
                (0..this.innerHandler.slots - 1).mapTo(result) {
                    object: ItemStackRenderPiece(
                            box.left + (it % columns) * 18,
                            box.top + (it / columns) * 18) {
                        override val isVisible: Boolean
                            get() = this@ColoredItemHandler.innerHandler.getStackInSlot(it).isEmpty

                        override fun getRenderStack()
                            = this@ColoredItemHandler.innerHandler.getFilterStack(it)
                    }
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
