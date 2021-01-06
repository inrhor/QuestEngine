package cn.inrhor.questengine.common.dialog

import java.util.regex.Matcher
import java.util.regex.Pattern

class TextAnimation(
    val id: String,
    val textContent: MutableList<String>
) {

    /**
     * 行数 对应 动态字符表
     */
    private var textMap: HashMap<Int, MutableList<String>> = LinkedHashMap<Int, MutableList<String>>()

    fun init() {
        var line = 0
        textContent.forEach { a ->
            // 对每一行

            // 分割 取 独立内容
            val pContent = Pattern.compile("<(.*)>")
            val content = pContent.matcher(a)
            // 获取动态总时长
            val timeLong = getAllTimeLong(content)
            while (content.find()) {

                // 分割 取 内容的属性
                val pAttribute = Pattern.compile("\\[(.*)]")
                val attribute = pAttribute.matcher(content.group())
                val delay = attribute.group(2).toInt()

                for (i in 0..timeLong) {
                    if (delay >= i) {
                        /*when (attribute.group(1)) {
                            "normal" -> {

                            }
                            "flash" -> {

                            }
                            "write" -> {
                                val speed = attribute.group(3)

                            }
                        }*/
                        if (!hasWrite(content)) {
                            val textList = mutableListOf(attribute.group(3))
                            textMap[line] = textList
                            continue
                        }
                        val textList = mutableListOf<String>()
                        
                        if (attribute.group(1) == "write") {

                        }else {

                        }

                    }
                }

            }
        }
    }

    private fun getAllTimeLong(content: Matcher): Int {
        // 总帧数
        var i = 0
        // 确定最终的延迟
        var finalDelay = 0
        // 分割 取 内容的属性
        val pAttribute = Pattern.compile("\\[(.*)]")
        val attribute = pAttribute.matcher(content.group())
        while (content.find()) {
            val delay = attribute.group().toInt()
            if (delay > finalDelay) {
                finalDelay = delay
            }
            // 若是打字型则增加帧数
            if (attribute.group(1) == "write") {
                val speedLong = attribute.group(3).toInt()
                // 根据字数增加帧数
                val textLong = attribute.group(4).length
                i += speedLong * textLong
            }
        }
        // 最终帧数
        i += finalDelay
        return i
    }

    private fun hasWrite(content: Matcher): Boolean {
        while (content.find()) {
            val pAttribute = Pattern.compile("\\[(.*)]")
            val attribute = pAttribute.matcher(content.group())
            val type = attribute.group(1)
            if (type == "write") return true
        }
        return false
    }

    /*private fun addTimeLong(attribute: Matcher): Int {
        if (attribute.group(1) == "write") {

        }
        return i
    }*/
}