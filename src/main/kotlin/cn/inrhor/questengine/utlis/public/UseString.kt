package cn.inrhor.questengine.utlis.public

import cn.inrhor.questengine.QuestEngine

object UseString {

    fun getLang(): String {
        return QuestEngine.config.getString("setting.lang")!!
    }
}