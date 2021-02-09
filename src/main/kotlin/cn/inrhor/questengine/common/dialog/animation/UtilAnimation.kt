package cn.inrhor.questengine.common.dialog.animation

import cn.inrhor.questengine.common.dialog.animation.text.FrameWrite
import cn.inrhor.questengine.common.dialog.animation.text.TagText
import java.util.regex.Pattern

class UtilAnimation {

    /**
     * 这一行动态标签的动画实现
     */
    fun theLineTextAnimation(
        line: Int,
        frame: Int,
        tagTextList: MutableList<TagText>,
        frameWriteMap: MutableMap<Int, MutableList<FrameWrite>>,
        isCancelsMap: MutableMap<Int, MutableList<Boolean>>): String {

        val contentList = mutableListOf<String>()
        val isCancels = mutableListOf<Boolean>()

        for (index in 0 until tagTextList.size) {
            val tagText = tagTextList[index]
            val frameWrite = frameWriteMap[line]!![index]
            val textFrame = frameWrite.textFrame
            val writeSpeed = frameWrite.writeSpeed
            val size = tagText.contentList.size

            if (textFrame >= size) {
                contentList.add(tagText.contentList[size-1])
                isCancels.add(false)
                continue
            }

            if (frame >= tagText.delay) {
                val content = tagText.contentList[textFrame]
                if (writeSpeed >= tagText.speed) {

                    if (contentList.size < index) {
                        for (i in 0 until index) {
                            contentList.add("")
                        }
                        contentList.add(index, content)
                    }else {
                        contentList.add(index, content)
                    }

                    frameWrite.textFrame++
                    frameWrite.writeSpeed = 0

                }else {
                    contentList.add(content)
                    frameWrite.writeSpeed++
                }
            }
        }
        if (isCancels.isNotEmpty() and (isCancels.size == tagTextList.size)) {
            isCancelsMap[line] = isCancels
        }

        var text = ""
        contentList.forEach {
            text += it
        }

        return text
    }

    fun isColor(str: String): Boolean {
        if (str.endsWith("&")) {
            val pattern = Pattern.compile("@&|@§")
            val matcher = pattern.matcher(str)
            while (matcher.find()) {
                return false
            }
            return true
        }
        val get = str.substring(str.length-2)
        return checkColor(get)
    }

    fun checkColor(src: String): Boolean {
        val pattern = Pattern.compile("&\\d|§\\d|&[a-zA-Z]|§[a-zA-Z]")
        val matcher = pattern.matcher(src)
        while (matcher.find()) {
            return true
        }
        return false
    }

    fun getValue(str: String, attribute: String) = str.replace("$attribute=", "")
}