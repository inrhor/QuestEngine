package cn.inrhor.questengine.common.dialog.animation

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
//    private var textMap: HashMap<Int, MutableList<String>> = LinkedHashMap<Int, MutableList<String>>()
    private var textMap: HashMap<Int, MutableList<Text>> = LinkedHashMap<Int, MutableList<Text>>()

    fun init() {
        var line = 0
        textContent.forEach { a ->
            // 对每一行

            // 获取动态总时长
            val timeLong = Util().getAllTimeLong(a)

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

                val a1 = attributes[1]
                val delay = Util().getValue(a1, "delay").toInt()
                val a0 = attributes[0]
                val text = Text(a0, delay)
                text.timeLong = timeLong

                var multiply = 0 // 用于write
                for (time in 0..timeLong) {
                    val textList = getTextContent(line)
                    if (delay <= time) {
                        if (!a.contains("<[write][delay=")) { // 如果这一行是静态的就不处理动画
//                            val textList = mutableListOf(attributes[2])
                            textMap[line] = textList
                            continue
                        }
                        if (a0 == "write") {
//                            MsgUtil.send("delay $delay   time $time   a2 $a1")
                            // 实现打字型内容
                            val nextTime = Util().getValue(attributes[2], "speed").toInt()
                            text.speed = nextTime
                            if (time == delay+(nextTime*multiply)) {
                                // 截取前面字符
                                val a3 = attributes[3]
                                val end = multiply+Util().colorNumber(a3)
                                val get = a3.substring(0, end)
                                if (!(get.endsWith("&") or get.endsWith("§"))) {
                                    MsgUtil.send("tttttttt")
                                    text.contentList.add(a3.substring(0, end))
                                    text.contentList.forEach {
                                        MsgUtil.send("text  $it")
                                    }
                                    textList.add(text)
                                }
                                if (a3.length == end) {
                                    addTextMap(line, textList)
                                    continue
                                }
                                multiply++
                            }
                        }/*else {
                            textList.add(attributes[2])
                            textMap[line] = textList
                        }*/
                    }
                }
            }
            line++
        }
    }

    /**
     * 根据行数获得这一行的标签内容
     */
    fun getTextContent(line: Int): MutableList<Text> {
        if (textMap.containsKey(line)) return textMap[line]!!
        return mutableListOf()
    }

    fun addTextMap(line: Int, textList: MutableList<Text>) {
        textMap[line] = textList
    }
}