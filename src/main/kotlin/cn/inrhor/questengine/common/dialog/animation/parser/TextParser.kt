package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.text.TextAnimation
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import java.util.regex.Pattern


/**
 * 注册对话传递文字列表，此类做解析并存储
 *
 */
class TextParser(private val textContents: MutableList<String>) {

    /**
     * 动态文字组列表
     *
     * 每行特定动态文字组模块
     */
    val dialogTextList = mutableListOf<TextDialogPlay>()

    fun init(type: String) {
        for (line in 0 until this.textContents.size) {

            val script = this.textContents[line]

            if (type == "dialog") {

                // 分割 取 独立标签
                val pContent = Pattern.compile("<(.*?)>")
                val indTag = pContent.matcher(script)

                val textAnimation = TextAnimation(indTag, dialogTextList)
                textAnimation.init()

            }else {



            }
        }
    }


}