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
    private var textMap: HashMap<Int, MutableList<TagText>> = LinkedHashMap<Int, MutableList<TagText>>()

    fun init() {
        var line = 0
        textContent.forEach { a ->
            // 对每一行

            // 分割 取 独立标签
            val pContent = Pattern.compile("<(.*?)>")
            val indTag = pContent.matcher(a)

            // 这一行的所有标签
            val textTagList = getTextContent(line)

            // 标签匹配索引
            var tagIndex = 0

            // 对独立标签而言
            while (indTag.find()) {

                // 分割 取 内容的属性
                val pAttribute = Pattern.compile("\\[(.*?)]")
                val attribute = pAttribute.matcher(indTag.group())

                val attributes = mutableListOf<String>()
                while (attribute.find()) {
                    attributes.add(attribute.group(1))
                }

                val abType = attributes[0]
                val abDelay = UtilAnimation().getValue(attributes[1], "delay").toInt()
                val tagText = TagText(abType, abDelay, tagIndex)
                tagIndex++
//                tagText.timeLong = UtilAnimation().getTimeLong(attributes)

                if (abType == "write") {
                    val abText = attributes[3]
                    val abSpeed = UtilAnimation().getValue(attributes[2], "speed").toInt()
                    tagText.speed = abSpeed-1
//                    var multiply = 0
                    var end = 2
                    for (index in 0..abText.length) {
                        // 截取前面字符
//                        val end = multiply+Util().colorNumber(abText)
                        if (UtilAnimation().isColor(abText.substring(0, end))) {
                            end++
                            continue
                        }

                        val get = abText.substring(0, end)

                        MsgUtil.send("check $get  "+UtilAnimation().isColor(get)+"  end "+end)

                        if (!UtilAnimation().isColor(get)) {
                            tagText.contentList.add(abText.substring(0, end))
                            end++
                        }

                        if (abText.length == end-1) {
                            if (!textTagList.contains(tagText)) {
                                textTagList.add(tagText)
                            }
                            addTextMap(line, textTagList)
                            break
                        }
                    }
                    continue
                }
            }
            line++
        }
    }

    /**
     * 根据行数获得这一行的标签内容
     */
    fun getTextContent(line: Int): MutableList<TagText> {
        if (textMap.containsKey(line)) return textMap[line]!!
        return mutableListOf()
    }

    fun addTextMap(line: Int, textList: MutableList<TagText>) {
        textMap[line] = textList
    }
}