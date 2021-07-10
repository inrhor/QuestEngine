package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.common.script.kether.KetherHandler

class QuestControl(val questID: String, val id: String, var scriptList: MutableList<String>) {

    fun run() {
        for (script in scriptList) KetherHandler.eval(script)
    }

}