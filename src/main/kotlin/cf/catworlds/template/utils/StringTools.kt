package cf.catworlds.template.utils

import org.bukkit.ChatColor.*
import org.bukkit.World

object StringTools {

    fun String.replaceHeart() = this.replace(Regex("<3( |$)"), "$RED❤ $WHITE")

}

fun World.Environment.getReadableName(): String = when(this) {
    World.Environment.NORMAL -> "主世界"
    World.Environment.NETHER -> "地獄"
    World.Environment.THE_END -> "終界"
}