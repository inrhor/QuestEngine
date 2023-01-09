package cn.inrhor.questengine.common.quest.enum

import cn.inrhor.questengine.api.manager.DataManager.teamData
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

enum class ModeType {
    PERSONAL {
        override fun objective(player: Player): Set<Player> {
            return setOf(player)
        } },
    COLLABORATION {
        override fun objective(player: Player): Set<Player> {
            return player.teamData()?.playerMembers()?: setOf(player)
        }
    };

    abstract fun objective(player: Player): Set<Player>

    fun lang(player: Player): String {
        return player.asLangText("MODE-TYPE-$this")
    }
}