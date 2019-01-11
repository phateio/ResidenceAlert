package cf.catworlds.template.utils

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration

import java.io.File
import java.io.IOException

object ConfigHelper {

    fun loadConfig(file: File): FileConfiguration {
        return if (!file.exists()) YamlConfiguration() else YamlConfiguration.loadConfiguration(file)
    }

    fun saveConfig(config: FileConfiguration, file: File): Boolean {
        if (!file.parentFile.exists())
            file.parentFile.mkdirs()
        try {
            config.options().copyDefaults(true)
            config.save(file)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

}
