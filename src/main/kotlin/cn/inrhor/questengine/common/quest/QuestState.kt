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
        when (str.uppercase(Locale.getDefault())) {
            "NOT_ACCEPT" -> return QuestState.NOT_ACCEPT
            "DOING" -> return QuestState.DOING
            "IDLE" -> return QuestState.IDLE
            "FINISH" -> return QuestState.FINISH
        }
        return QuestState.FAILURE
    }

}