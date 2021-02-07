package cn.inrhor.questengine.common.dialog.animation

import cn.inrhor.questengine.utlis.public.MsgUtil
import java.util.regex.Pattern

class UtilAnimation {
    fun getTimeLong(attributes: MutableList<String>): Int {
        // 帧数
        var i = 0
//        val delay = getValue(attributes[1], "delay").toInt()
        // 确定最终的延迟
//        var finalDelay = 0
//        if (delay > finalDelay) {
//            finalDelay = delay
//        }
        // 若是打字型则增加帧数
        if (attributes[0] == "write") {
            val speed = getValue(attributes[2], "speed").toInt()
            // 根据字数增加帧数
            val textLong = attributes[3].length
            i = speed * (textLong - colorNumber(attributes[3]))
            MsgUtil.send("speed  $speed text  $i  "+attributes[3]+"ilong "+colorNumber(attributes[3])+"  len "+textLong)
        }
        // 最终帧数
//        i += finalDelay
        MsgUtil.send("iii  $i")
        return i
    }

    /**
     * 获取 &颜色 出现的次数
     *
     */
    fun colorNumber(src: String): Int {
        var count = 0
        val pattern = Pattern.compile("&\\d|§\\d|&[a-zA-Z]|§[a-zA-Z]")
        val matcher = pattern.matcher(src)
        while (matcher.find()) {
            count++
        }
        return count*2
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

    /*fun getAnimationText(tagTextList: MutableList<TagText>, frame: Int): String {
        val textContent = mutableListOf<String>()
        tagTextList.forEach {
            if (it.delay >= frame) {
//                textContent.add(it.contentList[it.textFrame])
//                MsgUtil.send("textFrame  "+it.textFrame)
            }
        }
        textContent.forEach {
            MsgUtil.send("look  $it")
        }
        return textContent.toString().replace("(?:\\[|null|\\]| +)", "")
    }*/

    fun getValue(str: String, attribute: String) = str.replace("$attribute=", "")
}