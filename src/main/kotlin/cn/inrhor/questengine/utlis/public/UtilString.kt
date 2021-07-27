package cn.inrhor.questengine.utlis.public

import cn.inrhor.questengine.QuestEngine
import java.util.*

object UtilString {

    fun getLang() = QuestEngine.config.getString("setting.lang")!!

    val pluginTag = "&7&l[ &c&li &7&l]&7&l[ &3&lQuestEngine &7&l]"

    fun getJsonStr(list: MutableList<String>): String {
        var content = ""
        list.forEach {
            if (content.isEmpty()) {
                content = it
                return@forEach
            }
            content = "$content&r\n$it"
        }
        return content
    }
}