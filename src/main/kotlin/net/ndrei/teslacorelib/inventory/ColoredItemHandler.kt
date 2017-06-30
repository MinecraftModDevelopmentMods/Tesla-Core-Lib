package net.ndrei.teslacorelib.inventory

import com.google.common.collect.Lists
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider

/**
 * Created by CF on 2017-06-28.
 */
open class ColoredItemHandler(handler: IItemHandler, val color: EnumDyeColor, val name: String, val index: Int?, val boundingBox: BoundingRectangle)
    : FilteredItemHandler(handler), IContainerSlotsProvider, IGuiContainerPiecesProvider {

    constructor(handler: IItemHandler, color: EnumDyeColor, name: String, boundingBox: BoundingRectangle)
        : this(handler, color, name, null, boundingBox)

    private var containerItemHandler: IFilteredItemHandler? = null

    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
        return Lists.newArrayList<Slot>()
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        return Lists.newArrayList<IGuiContainerPiece>()
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
