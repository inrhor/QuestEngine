package cn.inrhor.questengine.utlis.public

import cn.inrhor.questengine.IElodieQuest

object UseString {

    fun getLang(): String {
        return IElodieQuest.config.getString("setting.lang")!!
    }
}