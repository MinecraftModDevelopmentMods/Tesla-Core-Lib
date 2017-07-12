package net.ndrei.teslacorelib.render

import net.ndrei.teslacorelib.utils.BlockCube

/**
 * Created by CF on 2017-07-12.
 */
interface IWorkAreaProvider {
    fun getWorkArea(): BlockCube
    fun getWorkAreaColor(): Int
}