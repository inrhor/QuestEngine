package cn.inrhor.questengine.common.quest

import io.izzel.taboolib.module.locale.TLocale
import java.util.*

enum class QuestState {
    NOT_ACCEPT,
    DOING,
    IDLE,
    FINISH,
    FAILURE
}

object QuestStateUtil {

    fun strToState(str: String): QuestState {
        return when (str.uppercase(Locale.getDefault())) {
            "NOT_ACCEPT" -> QuestState.NOT_ACCEPT
            "DOING" -> QuestState.DOING
            "IDLE" -> QuestState.IDLE
            "FINISH" -> QuestState.FINISH
            else -> QuestState.FAILURE
        }
    }

    fun stateToStr(state: QuestState): String {
        return when (state) {
            QuestState.NOT_ACCEPT -> "NOT_ACCEPT"
            QuestState.DOING -> "DOING"
            QuestState.IDLE -> "IDLE"
            QuestState.FINISH -> "FINISH"
            else -> "FAILURE"
        }
    }

    /**
     * 状态单位
     */
    fun stateUnit(state: QuestState): String {
        return when (state) {
            QuestState.DOING -> TLocale.asString("QUEST.STATE_DOING")
            QuestState.FAILURE -> TLocale.asString("QUEST.STATE_FAILURE")
            QuestState.FINISH -> TLocale.asString("QUEST.STATE_FINISH")
            else -> TLocale.asString("QUEST.STATE_IDLE")
        }
    }

}