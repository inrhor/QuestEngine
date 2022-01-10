package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.common.quest.ModeType

class QuestMode(val type: String, val amount: Int = -1, val shareData: Boolean) {

    fun modeType(): ModeType = ModeType.valueOf(type.uppercase())

}