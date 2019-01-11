package cf.catworlds.template.text

import java.util.ArrayList
import java.util.Random
import java.util.regex.Pattern

class TextFormatter @JvmOverloads constructor(rawStrings: List<String>, keys: List<String> = ArrayList()) {

    private val texts: Array<SingleText>?

    private inner class SingleText internal constructor(private val msgArray: Array<String>, private val replaceArray: IntArray) {

        fun format(vararg args: Any?): String {
            val output = StringBuilder()
            for (i in msgArray.indices) {
                val keyID = replaceArray[i]
                if (keyID > -1 && keyID < args.size && args[keyID] != null)
                    output.append(args[keyID])
                else
                    output.append(msgArray[i])
            }
            return output.toString()
        }
    }

    init {
        val textList = ArrayList<SingleText>()

        for (rawText in rawStrings) {
            val work = ArrayList<String>()
            val workR = ArrayList<Int>()
            val split = ArrayList<String>()
            val splitR = ArrayList<Int>()
            work.add(rawText.replace("\\n", "\n"))
            workR.add(-1)

            for (keyNumber in keys.indices) {
                val keyPattern = Pattern.compile(
                        "\\$\\{" + Pattern.quote(keys[keyNumber]) + "(,(?!\\})((((?<!\\\\)\\\\(\\\\\\\\)*\\})|[^}])+?))?\\}")

                split.clear()
                splitR.clear()
                val itS = work.iterator()
                val itR = workR.iterator()
                while (itS.hasNext()) {
                    val str = itS.next()
                    val strR = itR.next()
                    if (strR != -1) {
                        split.add(str)
                        splitR.add(strR)
                        continue
                    }
                    findKey(str, keyPattern, keyNumber, split, splitR)
                } // end of while (each part : work)
                work.clear()
                workR.clear()
                work.addAll(split)
                workR.addAll(splitR)
            } // end of for (keyNumber : keys)

            val msgArray = work.stream().toArray<String> { size -> arrayOfNulls(size) }
            val replaceArray = workR.stream().mapToInt { i -> i }.toArray()

            textList.add(SingleText(msgArray, replaceArray))

        } // end of for (rawText : rawStrings)

        this.texts = textList.toTypedArray()

    }

    private fun findKey(str: String, keyPattern: Pattern, keyNumber: Int, strAddTo: MutableList<String>, intAddTo: MutableList<Int>) {
        // find "${key}" or "${key,Default}" (use '\' to escape '}')
        val mat = keyPattern.matcher(str)

        // split and get key
        var pointer: Int
        pointer = 0
        while (mat.find()) {
            if (pointer != mat.start()) {
                strAddTo.add(str.substring(pointer, mat.start()))
                intAddTo.add(-1)
            }
            var findKey = mat.group()
            val defStart = findKey.indexOf(',')
            findKey = if (defStart != -1) // has default value
                findKey.substring(defStart, findKey.length - 1).replace("\\\\(.)".toRegex(), "$1")
            else
                ""
            strAddTo.add(findKey)
            intAddTo.add(keyNumber)
            pointer = mat.end()
        }
        if (pointer != str.length) {
            strAddTo.add(str.substring(pointer))
            intAddTo.add(-1)
        }
    }

    fun format(vararg args: Any?): String {
        return if (texts == null || texts.isEmpty()) "" else texts[Random().nextInt(texts.size)].format(*args)
    }

}
