package net.ndrei.teslacorelib.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.util.JsonUtils
import org.apache.logging.log4j.Logger
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

@Suppress("unused")
class ModConfigHandler(private val modId: String, private val modClass: Class<*>, private val logger: Logger, modConfigurationDirectory: File) {
    val configFolder: File

    init {
        this.configFolder = File(modConfigurationDirectory, this.modId)
        this.configFolder.mkdirs()
    }

    fun readExtraRecipesFile(fileName: String, callback: (json: JsonObject) -> Unit) {
        val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        val config = File(this.configFolder, "$fileName-base.json")

        if (!config.exists()) {
            val stream = this.modClass.getResourceAsStream("/assets/${this.modId}/extra-recipes/$fileName.json")
            if (stream == null) {
                this.logger.error("Could not locate extra recipes base resource file: '$fileName.json'.")
            }
            else {
                stream.use { s ->
                    if (config.createNewFile()) {
                        val writer = BufferedWriter(FileWriter(config, false))
                        writer.use { outs ->
                            s.bufferedReader().use { ins ->
                                var line = ins.readLine()
                                while (line != null) {
                                    outs.write(line + "\n")
                                    line = ins.readLine()
                                }
                            }
                        }
                    }
                    else {
                        this.logger.error("Could not create extra recipes file: '${config.path}'.")
                    }
                }
            }
        }

        fun readFile(file: File) {
            if (file.exists()) {
                file.bufferedReader().use {
                    val json = JsonUtils.fromJson(GSON, it, JsonElement::class.java)
                    if (json != null) {
                        if (json.isJsonArray) {
                            json.asJsonArray.forEach {
                                if (it.isJsonObject) {
                                    callback(it.asJsonObject)
                                }
                            }
                        }
                        else if (json.isJsonObject) {
                            callback(json.asJsonObject)
                        }
                    }
                }
            }
        }

        readFile(config)
        readFile(File(this.configFolder, "$fileName-extra.json"))
    }
}
