package cn.inrhor.questengine.common.dialog.animation

import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.regex.Pattern

/**
 * 对话配置传递的内容列表，此类处理动画并存储
 */
class TextAnimation(private val textContent: MutableList<String>) {

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
//            val timeLong = Util().getAllTimeLong(a)

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

                // 这一行的所有标签
                val textTagList = getTextContent(line)

                val abType = attributes[0]
                val abDelay = Util().getValue(attributes[1], "delay").toInt()
                val textClass = Text(abType, abDelay)
                textClass.timeLong = Util().getTimeLong(attributes)

                if (abType == "write") {
                    val abText = attributes[3]
                    val abSpeed = Util().getValue(attributes[2], "speed").toInt()
                    textClass.speed = abSpeed
                    var multiply = 0
                    for (index in 0..abText.length) {
                        // 截取前面字符
                        val end = multiply+Util().colorNumber(abText)
                        val get = abText.substring(0, end)
                        if (!(get.endsWith("&") or get.endsWith("§"))) {
                            textClass.contentList.add(abText.substring(0, end))
                        }
                        if (abText.length == end) {
                            MsgUtil.send("leng")
                            if (!textTagList.contains(textClass)) {
                                MsgUtil.send("no exist")
                                textTagList.add(textClass)
                            }
                            addTextMap(line, textTagList)
                            break
                        }
                        multiply++
                    }
                    continue
                }

//                var multiply = 0 // 用于write

/*                for (time in 0..timeLong) {
                    val textList = getTextContent(line)
                    if (delay <= time) {
                        if (!a.contains("<[write][delay=")) { // 如果这一行是静态的就不处理动画
                            MsgUtil.send("else $delay   time $time   a2 $attributes[2]")
                            text.contentList.add(attributes[2])
                            textList.add(text)
                            textMap[line] = textList
                            continue
                        }
                        if (a0 == "write") {
                            MsgUtil.send("delay $delay   time $time   a2 $a1")
                            // 实现打字型内容
                            val nextTime = Util().getValue(attributes[2], "speed").toInt()
                            text.speed = nextTime
//                            if (time == delay+(nextTime*multiply)) {
                                // 截取前面字符
                                val a3 = attributes[3]
                                val end = multiply+Util().colorNumber(a3)
                                val get = a3.substring(0, end)
                                if (!(get.endsWith("&") or get.endsWith("§"))) {
                                    MsgUtil.send("tttttttt")
                                    text.contentList.add(a3.substring(0, end))
                                    textList.add(text)
                                }
                                if (a3.length == end) {
                                    addTextMap(line, textList)
                                    continue
                                }
                                multiply++
//                            }
                        }*//*else { // 下次更新其它文字类型
                            MsgUtil.send("else $delay   time $time   a2 $attributes[2]")
                            text.contentList.add(attributes[2])
                            textList.add(text)
                            textMap[line] = textList
                        }*//*
                    }
                }*/
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