package net.ndrei.teslacorelib.inventory

import net.ndrei.teslacorelib.gui.FluidTankPiece

/**
 * Created by CF on 2017-06-28.
 */
class BoundingRectangle(val left: Int, val top: Int, val width: Int, val height: Int) {
    val right: Int
        get() = this.left + this.width
    val bottom: Int
        get() = this.top + this.height

    val isEmpty
        get() = (this === EMPTY)

    companion object {
        val EMPTY = BoundingRectangle(0, 0, 0, 0)

        fun slots(left: Int, top: Int, columns: Int, rows: Int) =
            BoundingRectangle(left, top, columns * 18, rows * 18)

        fun fluid(left: Int, top: Int) =
            BoundingRectangle(left, top, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)
    }
}
