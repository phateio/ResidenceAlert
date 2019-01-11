package cf.catworlds.template.text

import java.util.Arrays
import java.util.Collections

enum class BasicText(keys: Array<String> = emptyArray(), vararg def: String) {
    //	NoPermission("&cYou don't have permission."),
    NotPlayer(def = *arrayOf("你不是一個玩家")),
    ChangeWelCome(arrayOf("Name", "newString"), "你對 \${Name} 回來時說的話已經換成: \${newString}"),
    /** "Type"  */
    EntityTypeNotFound(arrayOf<String>("Type"), "&cEntityType: &e\${Type}&c not found."),
    /** "NumberString"  */
    NotANumber(arrayOf<String>("NumberString"), "&e\${NumberString}&c is not a number"),
    ReloadSuccess(def = *arrayOf("&aReload success!")),
    /** "Path", "ConfigValue", "ValueType", "DefaultValue"  */
    ConfigLoadError(arrayOf<String>("Path", "ConfigValue", "ValueType", "DefaultValue"), "&cConfig load fail [&e\${Path} &c= &e\${ConfigValue,Null}&c] : Cannot cast to type &b\${ValueType}&c, use default &e\${DefaultValue,Null}");

    internal val defaultTexts: List<String> = Collections.unmodifiableList(Arrays.asList(*def))
    internal val formatKeys: List<String> = Collections.unmodifiableList(Arrays.asList(*keys))
}

fun BasicText.format(vararg args: Any?): String = TextHelper.format(this, *args)