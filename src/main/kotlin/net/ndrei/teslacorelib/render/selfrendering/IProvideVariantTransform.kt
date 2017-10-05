package net.ndrei.teslacorelib.render.selfrendering

import net.minecraftforge.common.model.TRSRTransformation

interface IProvideVariantTransform {
    fun getTransform(variant: String): TRSRTransformation
}
