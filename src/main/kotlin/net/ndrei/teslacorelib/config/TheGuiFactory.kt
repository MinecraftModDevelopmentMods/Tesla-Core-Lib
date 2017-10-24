package net.ndrei.teslacorelib.config

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.common.Loader

class TheGuiFactory : IModGuiFactory {
    lateinit var modId: String
    lateinit var modName: String

    override fun hasConfigGui(): Boolean {
        return true
    }

    override fun createConfigGui(parentScreen: GuiScreen?): GuiScreen {
        return GuiConfigScreen(parentScreen, this.modId, this.modName)
    }

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement> {
        return mutableSetOf()
    }

    override fun initialize(minecraftInstance: Minecraft?) {
        this.modId = Loader.instance().activeModContainer()!!.modId
        this.modName = Loader.instance().activeModContainer()!!.name
    }

    class GuiConfigScreen(parent: GuiScreen?, val modId: String, modName: String) : GuiConfig(parent, getConfigElements(modId), modId, true, true, "$modName Config") {
        companion object {
            private fun getConfigElements(modId: String) = mutableListOf<IConfigElement>().also {
                val mod = Loader.instance().modList.firstOrNull { it.modId == modId } ?: return@also
                val config = (mod.mod as? IModConfigFlagsProvider)?.modConfigFlags?.configuration ?: return@also
                config.categoryNames
                    .map { config.getCategory(it) }
                    .filter { it.parent == null }
                    .forEach { category -> it.add(ConfigElement(category)) }
            }
        }

        override fun onGuiClosed() {
            super.onGuiClosed()

            val mod = Loader.instance().modList.firstOrNull { it.modId == this.modId } ?: return
            val config = (mod.mod as? IModConfigFlagsProvider)?.modConfigFlags ?: return
            config.checkIfConfigChanged()
        }
    }

    companion object {
        const val CLASS_NAME = "net.ndrei.teslacorelib.config.TheGuiFactory"
    }
}
