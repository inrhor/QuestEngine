package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.common.kether.KetherHandler

class QuestControl(val id: String, val type: String, var scriptList: MutableList<String>) {

    fun run() {
        for (script in scriptList) KetherHandler.eval(script)
    }

}