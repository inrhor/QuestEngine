package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay
import cn.inrhor.questengine.api.hologram.HoloIDManager


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

    fun init(dialogID: String, type: String) {
        for (line in 0 until this.textContents.size) {

            val script = this.textContents[line]

            val holoID = HoloIDManager.generate(dialogID, line, "text")
//                if (HoloIDManager.existEntityID(holoID))
            HoloIDManager.addEntityID(holoID)

            if (type == "dialog") {

//                val textAnimation = TextAnimationFail(dialogID, line, script, dialogTextList)
//                textAnimation.init()

            }
        }
    }


}