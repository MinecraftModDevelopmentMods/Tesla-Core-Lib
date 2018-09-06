package net.ndrei.teslacorelib.localization

import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer

class LocalizedModText(private val key: String, private vararg val params: Any) {
    private val args = mutableListOf<ITextComponent>()
    private var cachedFormat: TextFormatting? = null

    operator fun ITextComponent.unaryPlus() {
        this@LocalizedModText.args.add(this)
    }

    operator fun TextFormatting.unaryPlus() {
        this@LocalizedModText.cachedFormat = this
    }

    operator fun String.unaryPlus() {
        this@LocalizedModText.args.add(TextComponentString(this))
    }

    val textComponent: ITextComponent get() =
        TextComponentTranslation(this.key, *this.params.toMutableList().also { it.addAll(this.args) }.toTypedArray()).also {
            if (this.cachedFormat != null) {
                it.style.color = this.cachedFormat
            }
        }
}

fun localizeModString(container: BasicTeslaGuiContainer<*>, guiPieceType: String, key: String, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(container.entity.blockType.registryName, guiPieceType, key, init)

fun localizeModString(resource: ResourceLocation?, guiPieceType: String, key: String, init: (LocalizedModText.() -> Unit)? = null) = when {
    (resource == null) -> localizeModString(key, arrayOf(), init)
    else -> localizeModString(resource.namespace, guiPieceType, key, init)
}

fun localizeModString(modId: String, guiPieceType: String, key: String, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(null, modId, guiPieceType, key, arrayOf(), init)
fun localizeModString(modId: String, guiPieceType: String, key: String, params: Array<out Any>, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(null, modId, guiPieceType, key, params, init)

fun localizeModString(mainKey: String?, modId: String, guiPieceType: String, key: String, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(mainKey, modId, guiPieceType, key, arrayOf(), init)

fun localizeModString(mainKey: String?, modId: String, guiPieceType: String, key: String, params: Array<out Any>, init: (LocalizedModText.() -> Unit)? = null): ITextComponent {
    val translationKey = when {
        mainKey.isNullOrBlank() -> {
            val (finalKey, finalModId) = if (key.contains(':')) {
                val parts = key.split(':', limit = 2)
                Pair(parts[1], parts[0])
            } else Pair(key, modId)

            "gui.$finalModId.$guiPieceType.$finalKey".replace(' ', '_').toLowerCase()
        }
        else -> mainKey!!
    }

    return localizeModString(translationKey, params, init)
}

fun localizeModString(translationKey: String, init: (LocalizedModText.() -> Unit)? = null) =
    localizeModString(translationKey, arrayOf(), init)

fun localizeModString(translationKey: String, params: Array<out Any>, init: (LocalizedModText.() -> Unit)? = null): ITextComponent {
    val result = LocalizedModText(translationKey, *params)
    if (init != null) {
        result.init()
    }
    return result.textComponent
}

fun String.makeTextComponent(format: TextFormatting? = null): ITextComponent {
    val result = TextComponentString(this)
    if (format != null) {
        result.style.color = format
    }
    return result
}

fun Int.makeTextComponent(format: TextFormatting? = null) : ITextComponent {
    val result = TextComponentString(String.format("%,d", this))
    if (format != null) {
        result.style.color = format
    }
    return result
}

fun Long.makeTextComponent(format: TextFormatting? = null) : ITextComponent {
    val result = TextComponentString(String.format("%,d", this))
    if (format != null) {
        result.style.color = format
    }
    return result
}

fun localizeFluidAmount(amount: Int, format: TextFormatting? = null) =
    localizeModString(MOD_ID, GUI_FLUID_TANK, "fluid_amount_format") {
        if (format != null) {
            +format
        }
        +String.format("%,d", amount).makeTextComponent(format)
    }
