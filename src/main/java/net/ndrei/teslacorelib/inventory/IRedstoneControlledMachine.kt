package net.ndrei.teslacorelib.inventory

/**
 * Created by CF on 2017-06-29.
 */
interface IRedstoneControlledMachine {
    val allowRedstoneControl: Boolean
    val redstoneControl: RedstoneControl
    fun toggleRedstoneControl()

    enum class RedstoneControl {
        AlwaysActive {
            override fun getNext() = RedstoneOn
            override fun canRun(getter: () -> Int) = true
        },
        RedstoneOn {
            override fun getNext() = RedstoneOff
            override fun canRun(getter: () -> Int) = (getter() > 0)
        },
        RedstoneOff {
            override fun getNext() = AlwaysActive
            override fun canRun(getter: () -> Int) = (getter() == 0)
        };

        abstract fun getNext(): RedstoneControl

        abstract fun canRun(getter: () -> Int): Boolean
    }
}