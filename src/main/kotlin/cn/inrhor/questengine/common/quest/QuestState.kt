package cn.inrhor.questengine.common.quest

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

enum class QuestState {
    NOT_ACCEPT,
    DOING,
    FINISH,
    FAILURE
}

fun String.toState(): QuestState {
    return when (this.uppercase()) {
        "NOT_ACCEPT" -> QuestState.NOT_ACCEPT
        "DOING" -> QuestState.DOING
        "FINISH" -> QuestState.FINISH
        else -> QuestState.FAILURE
    }
}

fun Int.toState(): QuestState {
    return when (this) {
        0 -> QuestState.NOT_ACCEPT
        1 -> QuestState.DOING
        2 -> QuestState.DOING
        3 -> QuestState.FINISH
        else -> QuestState.FAILURE
    }
}

fun QuestState.toStr(): String {
    return when (this) {
        QuestState.NOT_ACCEPT -> "NOT_ACCEPT"
        QuestState.DOING -> "DOING"
        QuestState.FINISH -> "FINISH"
        else -> "FAILURE"
    }
}

fun QuestState.toInt(): Int {
    return when (this) {
        QuestState.NOT_ACCEPT -> 0
        QuestState.DOING -> 1
        QuestState.FINISH -> 3
        else -> 4
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