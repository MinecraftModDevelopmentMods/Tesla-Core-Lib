package net.ndrei.teslacorelib.annotations

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by CF on 2017-07-07.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterFluid

object AutoRegisterFluidHandler : BaseAnnotationHandler<Fluid>({ it, _, _ ->
    FluidRegistry.registerFluid(it)
    FluidRegistry.addBucketForFluid(it)
}, AutoRegisterFluid::class)
