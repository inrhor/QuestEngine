package cn.inrhor.questengine.utlis.public

import cn.inrhor.questengine.QuestEngine

object UseString {

    fun getLang() = QuestEngine.config.getString("setting.lang")!!

    val pluginTag = "&7&l[ &c&li &7&l]&7&l[ &3&lQuestEngine &7&l]"
}