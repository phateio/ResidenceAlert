package cf.catworlds.template

import cf.catworlds.template.command.MainCommand
import cf.catworlds.template.listener.EventsListener
import cf.catworlds.template.text.TextHelper
import cf.catworlds.template.utils.ConfigHelper
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TemplatePlugin : JavaPlugin() {
    companion object {
        lateinit var instance: TemplatePlugin
            private set
    }

    override fun onEnable() {
        instance = this
        loadText()
        loadConfig()
        loadCommand()
        loadListener()
    }

    override fun onDisable() {
    }

    internal fun loadText() {
        val langFile = File(dataFolder, "lang${File.separator}message.yml")
        val config = ConfigHelper.loadConfig(langFile)
        TextHelper.init(config)
        ConfigHelper.saveConfig(config, langFile)
    }

    fun loadConfig(sender: CommandSender = server.consoleSender) {
        val config = config
        Setting.loadSetting(config, sender)
        config.options().copyDefaults(true)
        saveConfig()
    }

    private fun loadCommand() {
        getCommand("test")!!.setExecutor(MainCommand(this))
    }

    private fun loadListener() {
        server.pluginManager.registerEvents(EventsListener(), this)
    }

}
