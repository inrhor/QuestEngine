package cn.inrhor.questengine.common.dialog.optional.holo.animation.parser

import cn.inrhor.questengine.common.dialog.optional.holo.animation.text.DialogTextAnimation
import cn.inrhor.questengine.common.dialog.optional.holo.animation.text.TextDialog
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
    private var dialogTextList = mutableListOf<TextDialog>()

    fun init(type: String) {
        for (line in 0 until this.textContents.size) {

            val script = this.textContents[line]

            if (type.equals("dialog")) {

                // 分割 取 独立标签
                val pContent = Pattern.compile("<(.*?)>")
                val indTag = pContent.matcher(script)

                val textAnimation = DialogTextAnimation(indTag, dialogTextList)
                textAnimation.init()

            }else {



            }
        }
    }


}