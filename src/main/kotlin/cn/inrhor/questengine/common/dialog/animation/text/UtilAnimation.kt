package cn.inrhor.questengine.common.dialog.animation.text

import java.util.regex.Pattern

class UtilAnimation {

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

}