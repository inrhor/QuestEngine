package cn.inrhor.questengine.common.dialog.animation

import java.util.regex.Pattern

class Util {
    fun getAllTimeLong(a: String): Int {
        // 总帧数
        var i = 0
        // 确定最终的延迟
        var finalDelay = 0
        // 分割 取 内容的属性
        val pAttribute = Pattern.compile("\\[(.*?)]")
        val pContent = Pattern.compile("<(.*?)>")
        val indTag = pContent.matcher(a)
        while (indTag.find()) {
            val attribute = pAttribute.matcher(indTag.group(1))
            val attributes = mutableListOf<String>()
            while (attribute.find()) {
                attributes.add(attribute.group(1))
            }
            val delay = getValue(attributes[1], "delay").toInt()
            if (delay > finalDelay) {
                finalDelay = delay
            }
            // 若是打字型则增加帧数
            if (attributes[0] == "write") {
                val speedLong = getValue(attributes[2], "speed").toInt()
                // 根据字数增加帧数
                val textLong = attributes[3].length
                i += speedLong * textLong
            }
        }
        // 最终帧数
        i += finalDelay
        return i
    }

    /**
     * 获取 &颜色 出现的次数
     *
     */
    fun colorNumber(src: String): Int {
        var count = 0
        val pattern = Pattern.compile("&\\d|§\\d|&[a-zA-Z]|§[a-zA-Z]")
        val matcher = pattern.matcher(src)
        while (matcher.find()) {
            count++
        }
        return count
    }

    fun getValue(str: String, attribute: String) = str.replace("$attribute=", "")
}