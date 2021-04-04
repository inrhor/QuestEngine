package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.text.TagText
import cn.inrhor.questengine.common.dialog.animation.UtilAnimation
import cn.inrhor.questengine.common.kether.KetherHandler
import java.util.regex.Pattern

/**
 * 对话配置传递的文本内容列表，此类处理动画并存储
 */
class TextAnimation(private val textContents: MutableList<String>) {

    /**
     * 行数 对应 动态字符表
     *
     * 其中 动态字符表 根据 帧数，但要注意长度
     */
    private var textMap: HashMap<Int, MutableList<TagText>> = LinkedHashMap<Int, MutableList<TagText>>()

    fun init() {
        var line = 0
        textContents.forEach { a ->
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
                val script = indTag.group(1)
                if (indTag.group(1).startsWith("iHoloWrite")) {
                    val holoWrite = KetherHandler.evalHoloWrite(script)
                    val tagText =
                        TagText(
                            "write",
                            holoWrite.delay,
                            holoWrite.speedWrite-1,
                            tagIndex
                        )
                    tagIndex++

                    var end = 2
                    val abText = holoWrite.text
                    for (index in 0..abText.length) {
                        // 截取前面字符
                        if (UtilAnimation().isColor(abText.substring(0, end))) {
                            end++
                            continue
                        }

                        val get = abText.substring(0, end)

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