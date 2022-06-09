package cn.inrhor.questengine.common.quest.enum

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

enum class StateType(val int: Int) {
    NOT_ACCEPT(0),
    DOING(1),
    FINISH(2),
    FAILURE(3);

    companion object {
        private val values = values()
        fun fromInt(value: Int) = values.firstOrNull { it.int == value }?: NOT_ACCEPT
    }

    fun toUnit(player: Player): String {
        return player.asLangText("STATE-TYPE-$this")
    }
}