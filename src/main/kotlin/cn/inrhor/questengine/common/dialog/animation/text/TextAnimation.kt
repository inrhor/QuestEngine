package cn.inrhor.questengine.common.dialog.animation.text

import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.animation.text.type.TextWrite
import cn.inrhor.questengine.script.kether.evalTextWrite
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 处理一行多个独立标签集合动态文字
 */
class TextAnimation(val dialogID: String, val line: Int, val script: String, val pattern: Pattern, val dialogTextList: MutableList<TextDialogPlay>) {

    fun init() {
        val texts = mutableListOf<String>()
        val sendChat = mutableListOf<String>()
        var indTag = pattern.matcher(script)
        val delay = minDelay(indTag)
        indTag = pattern.matcher(script)
        // 对独立标签而言
        var firstFrame = true
        while (indTag.find()) {
            val script = indTag.group(1)
            val iU = script.uppercase()
            if (iU.startsWith("TEXTWRITE") || iU.startsWith("EMPTYWRITE")) {
                val textWrite = evalTextWrite(script)
                val abDelay = textWrite.delay
                val abSpeed = textWrite.speedWrite
                val abText = textWrite.text
                if (textWrite.sendChat) {
                    sendChat.add(abText)
                }
                val emptyWrite = textWrite.type == TextWrite.Type.EMPTYWRITE
                writeType(abDelay, delay, texts, abSpeed, " &r$abText", firstFrame, emptyWrite)
            }
            firstFrame = false
        }

        val holoID = HoloIDManager.generate(dialogID, line, "text")
//                if (HoloIDManager.existEntityID(holoID))
        HoloIDManager.addEntityID(holoID)

        val textAnimation = TextDialogPlay(holoID, texts, delay, sendChat)
        dialogTextList.add(textAnimation)
    }

    private fun writeType(abDelay: Int, delay: Int,
                          texts: MutableList<String>, abSpeed: Int, abText: String,
                          firstFrame: Boolean, sendChat: Boolean) {
        var frame = 0
        var frameTextIndex = 0 // 第x行的文字帧
        if (abDelay > delay) frameTextIndex += abDelay
        var end = 2; var speed = 1
        val ts = texts.size - 1
        var length = abText.length
        for (index in 0..length+length*abSpeed) {
            val getText = abText.substring(0, end)
            if (!UtilAnimation().isColor(getText)) {

                if (speed >= abSpeed) {
                    speed = 1; end++; frame++
                } else speed++

                if (texts.isEmpty()) {  // 首次由于延迟
                    for (i in 0..frameTextIndex) {
                        if (i == frameTextIndex) {
                            texts.add(getText)
                        }else texts.add("")
                    }
                }else if (texts.size > frameTextIndex) { // 在已有帧数内
                    texts[frameTextIndex] = texts[frameTextIndex]+getText
                }else { // 新帧数
                    if (firstFrame) {
                        texts.add(getText)
                    }else {
                        val long = frameTextIndex-texts.size
                        if (long > 0) {
                            for (i in 0 until long) {
                                texts.add(texts[ts])
                            }
                        }
                        texts.add(texts[ts]+getText)
                    }
                }
                frameTextIndex++
            }else { end++; length -= 1 }
            if (frame >= length-1)  {
                for (i in frameTextIndex until texts.size) {
                    texts[i] = texts[i]+abText
                }
                break
            }
        }
        if (sendChat) {
            for (s in 0..abSpeed) {
                texts.add(texts.last())
            }
            texts.add(texts.last().replace(abText, ""))
        }
    }

    private fun minDelay(tags: Matcher): Int {
        val t = mutableListOf<TextWrite>()
        while (tags.find()) {
            val script = tags.group(1)
            t.add(evalTextWrite(script))
        }
        val nt = t.sortedBy { TextWrite -> TextWrite.delay }
        return nt[0].delay
    }

}