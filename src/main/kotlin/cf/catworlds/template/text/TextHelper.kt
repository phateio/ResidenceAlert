package cf.catworlds.template.text

import net.md_5.bungee.api.ChatColor
import org.bukkit.configuration.file.FileConfiguration

import java.util.HashMap

object TextHelper {
    private val text_map = HashMap<BasicText, TextFormatter>()

    fun format(text: BasicText, vararg args: Any?): String {
        return text_map[text]!!.format(*args)
    }

    fun init(config: FileConfiguration) {
        text_map.clear()
        for (text in BasicText.values()) {
            val textPATH = text.name
            if (!config.isList(textPATH))
                config.set(textPATH, text.defaultTexts)
            val read = config.getStringList(textPATH)
            read.replaceAll { line -> ChatColor.translateAlternateColorCodes('&', line) }
            val formater = TextFormatter(read, text.formatKeys)
            text_map[text] = formater
        }
    }

}
