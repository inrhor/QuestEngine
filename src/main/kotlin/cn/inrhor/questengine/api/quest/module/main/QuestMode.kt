package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.common.quest.ModeType
import taboolib.library.configuration.PreserveNotNull

@PreserveNotNull
class QuestMode(val type: String, val amount: Int, val shareData: Boolean) {

    constructor(): this("PERSONAL", -1, false)

    fun modeType(): ModeType = ModeType.valueOf(type.uppercase())

}