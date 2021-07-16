package cn.inrhor.questengine.common.quest

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

}