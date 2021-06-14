package cn.inrhor.questengine.common.dialog.optional.holo.animation.text

import cn.inrhor.questengine.common.kether.KetherHandler
import java.util.*
import java.util.regex.Matcher

/**
 * 处理一行多个独立标签集合动态文字
 */
class TextAnimation(val indTag: Matcher, val dialogTextList: MutableList<TextDialogPlay>) {

    fun init() {
        var frame = 0
        var delay = 0
        val texts = mutableListOf<String>()

        // 对独立标签而言
        while (indTag.find()) {
            val script = indTag.group(1)
            var frameTextIndex = 0
            if (indTag.group(1).uppercase(Locale.getDefault()).startsWith("TEXTWRITE")) {
                val textWrite = KetherHandler.evalTextWrite(script)
                val abDelay = textWrite.delay
                val abSpeed = textWrite.speedWrite
                val abText = textWrite.text
                val abTextLength = abText.length

                if (delay > abDelay && abDelay < 0) delay = abDelay

                var end = 2; var speed = 0
                for (index in 0..abTextLength+abTextLength*abSpeed) {
                    if (UtilAnimation().isColor(abText.substring(0, end))) {
                        val getText = abText.substring(0, end)
                        if (frame > 0 && speed >= abSpeed) { speed = 0; end++ }else speed++
                        if (texts.size >= frameTextIndex) {
                            texts[frameTextIndex] = getText
                        } else texts.add(getText)
                        frameTextIndex++; frame++
                    }
                }
            }
        }

        val textAnimation = TextDialogPlay(texts, delay)
        dialogTextList.add(textAnimation)
    }

}