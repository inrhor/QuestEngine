package cn.inrhor.questengine.common.dialog.animation.text

import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 处理一行多个独立标签集合动态文字
 */
class TextAnimation(val dialogID: String, val line: Int, val script: String, val pattern: Pattern, val dialogTextList: MutableList<TextDialogPlay>) {

    fun init() {
        val texts = mutableListOf<String>()
        var indTag = pattern.matcher(script)
        val delay = minDelay(indTag)
        indTag = pattern.matcher(script)
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

                if (abDelay > delay) frameTextIndex += abDelay
                MsgUtil.send("frameIndex  $frameTextIndex")

                var end = 2; var speed = 0
                for (index in 0..(abTextLength+abTextLength*abSpeed)) {
                    if (!UtilAnimation().isColor(abText.substring(0, end))) {
                        val getText = abText.substring(0, end)

                        if (speed >= abSpeed) { speed = 0; end++; frame++ } else speed++

                        if (texts.isEmpty()) {
                            for (i in 0..frameTextIndex) {
                                if (i == frameTextIndex) {
                                    texts.add(getText)
                                }else texts.add("")
                            }
                        }else if (texts.size > frameTextIndex) {
                            MsgUtil.send("wqe  ff  "+texts[frameTextIndex]+getText)
                            texts[frameTextIndex] = texts[frameTextIndex]+getText
                        }else {
                            val ts = texts.size
                            val fti = frameTextIndex
                            for (i in ts..fti) {
                                if (i == fti) {
                                    if (firstFrame) {
                                        texts.add(getText)
                                        MsgUtil.send("firstfirstfirstFrame  "+getText)
                                    }else {
                                        MsgUtil.send("tststs  "+texts[ts]+getText)
                                        texts.add(texts[ts]+getText)
                                    }
                                }else {
                                    MsgUtil.send("eeeeeeeeeeeeeeeeeeeeeeeeeee")
                                    MsgUtil.send("size-1    "+texts[texts.size-1])
                                    texts.add(texts[texts.size-1])
                                }
                            }
                        }
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

    fun minDelay(tags: Matcher): Int {
        val t = mutableListOf<TextWrite>()
        while (tags.find()) {
            val script = tags.group(1)
            val textWrite = KetherHandler.evalTextWrite(script)
            t.add(textWrite)
        }
        val nt = t.sortedBy { TextWrite -> TextWrite.delay }
        return nt[0].delay
    }

}