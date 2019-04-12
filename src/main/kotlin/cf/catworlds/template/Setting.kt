package cf.catworlds.template

import cf.catworlds.template.text.BasicText
import cf.catworlds.template.text.TextHelper
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

object Setting {

    @SettingInfo(path = "Check.Period")
    var checkPeriod: Int = 0
    @SettingInfo(path = "Check.Damage")
    var checkDamage: Int = 0

    fun loadSetting(config: FileConfiguration, sender: CommandSender) {
        // set default value
        checkPeriod = 10
        checkDamage = 3
        // save default then load
        default_load(config, sender)
        // post value
    }

    fun loadSetting(config: FileConfiguration, plugin: Plugin) {
        loadSetting(config, plugin.server.consoleSender)
    }

    private fun default_load(config: FileConfiguration, sender: CommandSender) {
        for (field in Setting::class.declaredMemberProperties) {
            val info = field.annotations.find { it is SettingInfo } as? SettingInfo ?: continue
            field.isAccessible = true
            val path = info.path
            config.addDefault(path, field.getValue())
            try {
                field.set(config.get(path))
//                field.set(Setting, config.get(path))
            } catch (e: IllegalArgumentException) {
                sender.sendMessage(TextHelper.format(BasicText.ConfigLoadError, path, "" + config.get(path), field.returnType.javaType.typeName, "" + config.defaults!!.get(path)))
            } catch (ignore: Throwable) {
                ignore.printStackTrace()
            }

        }
    }

    private fun KProperty1<Setting, *>.getValue(): Any? {
        try {
            return this.get(Setting)
        } catch (ignore: Throwable) {
        }

        // TODO read fail message
        return null
    }

    fun KProperty1<Setting, *>.set(value: Any?) {
        (this as KMutableProperty1<Setting, Any?>).set(Setting, value)
    }

    @Target(AnnotationTarget.PROPERTY)
    annotation class SettingInfo(val path: String)
}
