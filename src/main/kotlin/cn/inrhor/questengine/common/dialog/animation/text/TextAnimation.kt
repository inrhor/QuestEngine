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
        val texts = mutableListOf<String>()
        var delay = 0
        // 对独立标签而言
        while (indTag.find()) {
            var frame = 0
            val script = indTag.group(1)
            var frameTextIndex = 0
            if (script.uppercase(Locale.getDefault()).startsWith("TEXTWRITE")) {
                val textWrite = KetherHandler.evalTextWrite(script)
                val abDelay = textWrite.delay
                val abSpeed = textWrite.speedWrite
                val abText = textWrite.text
                val abTextLength = abText.length
                var length = abTextLength

                if (delay > abDelay && abDelay < 0) delay = abDelay

                if (abDelay > delay) frameTextIndex += abDelay
                MsgUtil.send("frameIndex  $frameTextIndex")

                var end = 2; var speed = 0
                for (index in 0..(abTextLength+abTextLength*abSpeed)) {
                    if (!UtilAnimation().isColor(abText.substring(0, end))) {
                        val getText = abText.substring(0, end)
                        if (speed >= abSpeed) { speed = 0; end++; frame++ } else speed++
                        if (texts.size > frameTextIndex) {
                            texts[frameTextIndex] = texts[frameTextIndex]+getText
                        } else texts.add(getText)
                        frameTextIndex++
                    }else { end++; length -= 1 }
                    if (frame >= length-1)  {
                        for (i in frameTextIndex until texts.size) {
                            MsgUtil.send("index  $i   "+texts.size)
                            texts[i] = texts[i]+abText
                        }
                        break
                    }
                }
            }
        }

        for (i in texts) {
            MsgUtil.send("ee  $i")
        }

        val holoID = HoloIDManager().generate(dialogID, line, "text")
//                if (HoloIDManager().existEntityID(holoID))
        HoloIDManager().addEntityID(holoID)

        val textAnimation = TextDialogPlay(holoID, texts, delay)
        dialogTextList.add(textAnimation)
    }

}