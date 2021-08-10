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

fun String.toState(): QuestState {
    return when (this.uppercase(Locale.getDefault())) {
        "NOT_ACCEPT" -> QuestState.NOT_ACCEPT
        "DOING" -> QuestState.DOING
        "IDLE" -> QuestState.IDLE
        "FINISH" -> QuestState.FINISH
        else -> QuestState.FAILURE
    }
}

fun QuestState.toStr(): String {
    return when (this) {
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
fun QuestState.toUnit(player: Player): String {
    return when (this) {
        QuestState.DOING -> player.asLangText("QUEST-STATE_DOING")
        QuestState.FAILURE -> player.asLangText("QUEST-STATE_FAILURE")
        QuestState.FINISH -> player.asLangText("QUEST-STATE_FINISH")
        else -> player.asLangText("QUEST-STATE_IDLE")
    }
}