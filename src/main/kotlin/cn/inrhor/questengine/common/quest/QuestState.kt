package cn.inrhor.questengine.common.quest

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
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
    fun stateUnit(player: Player, state: QuestState): String {
        return when (state) {
            QuestState.DOING -> player.asLangText("QUEST.STATE_DOING")?: return "null"
            QuestState.FAILURE -> player.asLangText("QUEST.STATE_FAILURE")?: return "null"
            QuestState.FINISH -> player.asLangText("QUEST.STATE_FINISH")?: return "null"
            else -> player.asLangText("QUEST.STATE_IDLE")?: return "null"
        }
    }

}