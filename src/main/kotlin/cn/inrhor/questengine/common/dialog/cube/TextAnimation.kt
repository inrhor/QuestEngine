package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.regex.Pattern

class TextAnimation(
    val textContent: MutableList<String>
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

            // 获取动态总时长
            val timeLong = getAllTimeLong(a)

            // 分割 取 独立标签
            val pContent = Pattern.compile("<(.*?)>")
            val indTag = pContent.matcher(a)

            // 对独立标签而言
            while (indTag.find()) {

                // 分割 取 内容的属性
                val pAttribute = Pattern.compile("\\[(.*?)]")
                val attribute = pAttribute.matcher(indTag.group())

                val attributes = mutableListOf<String>()
                while (attribute.find()) {
                    attributes.add(attribute.group(1))
                }

                val delay = getValue(attributes[1], "delay").toInt()

                var multiply = 0 // 用于write
                for (time in 0..timeLong) {
                    // 如果 该属性的delay 小于当前的动态帧
                    if (delay <= time) {
                        MsgUtil.send("delay $delay   time $time")
                        if (!a.contains("<[write][delay=")) { // 如果这一行是静态的就不处理动画
                            val textList = mutableListOf(attributes[2])
                            textMap[line] = textList
                            continue
                        }
                        val textList = getTextContent(line)
                        val a2 = attributes[2]
                        if (attributes[0] == "write") {
                            // 实现打字型内容
                            val nextTime = getValue(a2, "speed").toInt()
                            if (time == delay+(nextTime*multiply)) {
                                // 截取前面字符
                                val a3 = attributes[3]
                                val end = multiply+colorNumber(a3)
                                val get = a3.substring(0, end)
                                if (!(get.endsWith("&") or get.endsWith("§"))) {
                                    textList.add(a3.substring(0, end))
                                    textMap[line] = textList
                                    if (a3.length == end) return
                                }
                                multiply++
                            }
                        }else {
                            textList.add(a2)
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

    private fun getAllTimeLong(a: String): Int {
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
    private fun colorNumber(src: String): Int {
        var count = 0
        val pattern = Pattern.compile("&\\d|§\\d|&[a-zA-Z]|§[a-zA-Z]")
        val matcher = pattern.matcher(src)
        while (matcher.find()) {
            count++
        }
        return count
    }

    private fun getValue(str: String, attribute: String) = str.replace("$attribute=", "")
}