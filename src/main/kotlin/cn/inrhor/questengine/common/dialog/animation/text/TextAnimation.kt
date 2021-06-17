package cn.inrhor.questengine.common.dialog.animation.text

import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.*
import java.util.regex.Matcher

/**
 * 处理一行多个独立标签集合动态文字
 */
class TextAnimation(val dialogID: String, val line: Int, val indTag: Matcher, val dialogTextList: MutableList<TextDialogPlay>) {

    fun init() {
        var frame = 0
        var delay = 0
        val texts = mutableListOf<String>()

        // 对独立标签而言
        while (indTag.find()) {
            val script = indTag.group(1)
            var frameTextIndex = 0
            if (indTag.group(1).uppercase(Locale.getDefault()).startsWith("TEXTWRITE")) {
                MsgUtil.send("eee  $script")
                val textWrite = KetherHandler.evalTextWrite(script)
                val abDelay = textWrite.delay
                val abSpeed = textWrite.speedWrite
                val abText = textWrite.text
                val abTextLength = abText.length
                var length = abTextLength

                if (delay > abDelay && abDelay < 0) delay = abDelay

                var end = 2; var speed = 0
                MsgUtil.send("len  "+(abTextLength+abTextLength*abSpeed))
                for (index in 0..(abTextLength+abTextLength*abSpeed)) {
                    if (!UtilAnimation().isColor(abText.substring(0, end))) {
                        val getText = abText.substring(0, end)
                        MsgUtil.send("test11  $getText")
                        if (speed >= abSpeed) { speed = 0; end++; frame++ } else speed++
                        if (texts.size > frameTextIndex) {
                            texts[frameTextIndex] = getText
                        } else texts.add(getText)
//                        texts.add(getText)
                        frameTextIndex++
                    }else { end++; length -= 1 }
                    MsgUtil.send("frame  $frame   ab  $abTextLength  lent $length")
                    if (frame >= length-1) break
                }
            }
        }

        val holoID = HoloIDManager().generate(dialogID, line, "text")
//                if (HoloIDManager().existEntityID(holoID))
        HoloIDManager().addEntityID(holoID)

        val textAnimation = TextDialogPlay(holoID, texts, delay)
        dialogTextList.add(textAnimation)
    }

}