package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.utlis.removeAt
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

data class AcceptAddon(var auto: Boolean = false, var condition: String = "") {
    fun delCondition(int: Int) {
        condition = condition.removeAt(int)
    }

    fun autoLang(player: Player) =
        if (auto) player.asLangText("QUEST-ACCEPT-WAY-AUTO")
        else player.asLangText("QUEST-ACCEPT-WAY-COMMON")
}