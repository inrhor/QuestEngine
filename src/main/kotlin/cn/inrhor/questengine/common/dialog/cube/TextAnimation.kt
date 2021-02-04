package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.regex.Matcher
import java.util.regex.Pattern

class TextAnimation(
    private val textContent: MutableList<String>
) {

    /**
     * 行数 对应 动态字符表
     *
     * 其中 动态字符表 根据 帧数，但要注意长度
     */
    private var textMap: HashMap<Int, MutableList<String>> = LinkedHashMap<Int, MutableList<String>>()

    fun init() {
        var line = 0
        textContent.forEach { a ->
            // 对每一行

            // 分割 取 独立标签
            val pContent = Pattern.compile("<(.*)>")
            val indTag = pContent.matcher(a)
            // 获取动态总时长
            val timeLong = getAllTimeLong(indTag)

            // 对独立标签而言
            while (indTag.find()) {

                // 分割 取 内容的属性
                val pAttribute = Pattern.compile("\\[(.*)]")
                val attribute = pAttribute.matcher(indTag.group())
                val delay = attribute.group(2).toInt()

                var multiply = 0 // 用于write
                for (time in 0..timeLong) {
                    if (delay >= time) {
                        if (!hasWrite(indTag)) {
                            val textList = mutableListOf(attribute.group(3))
                            textMap[line] = textList
                            continue
                        }
                        val textList = getTextContent(line)
                        if (attribute.group(1) == "write") {
                            // 实现打字型内容
                            val nextTime = attribute.group(3).toInt()
                            if (time == delay+(nextTime*multiply)) {
                                textList.add(attribute.group(3).substring(0, multiply))
                                textMap[line] = textList
                                multiply++
                            }
                        }else {
                            textList.add(attribute.group(3))
                            textMap[line] = textList
                        }
                    }
                }

            }
            line++
        }
    }

    /**
     * 根据行数获得动态字符表内容
     */
    fun getTextContent(line: Int): MutableList<String> {
        if (textMap.containsKey(line)) return textMap[line]!!
        return mutableListOf()
    }

    private fun getAllTimeLong(content: Matcher): Int {
        // 总帧数
        var i = 0
        // 确定最终的延迟
        var finalDelay = 0
        // 分割 取 内容的属性
        val pAttribute = Pattern.compile("\\[(.*)]")
        while (content.find()) {
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
}