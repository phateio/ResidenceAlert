package cf.catworlds.template.command

import cf.catworlds.template.TemplatePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.util.StringUtil
import java.util.*

class MainCommand(private val plugin: TemplatePlugin) : TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty())
            return false
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String> {
        val completions = ArrayList<String>()
        if (args.size == 1)
            StringUtil.copyPartialMatches<MutableList<String>>(args[0], FirstLevel, completions)
        // if (args.length == 2 && args[0].equalsIgnoreCase("SubCommand1"))
        // Next level ...
        return completions
    }

    companion object {
        private val FirstLevel = Arrays.asList("a", "b", "c")
    }

}