package cn.inrhor.questengine.common.quest.enum

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

enum class StateType(val int: Int) {
    NOT_ACCEPT(0),
    DOING(1),
    FINISH(2),
    FAILURE(3);

    fun toUnit(player: Player): String {
        return player.asLangText("STATE-TYPE-$this")
    }
}