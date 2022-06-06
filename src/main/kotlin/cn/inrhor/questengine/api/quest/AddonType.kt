package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.database.data.teamData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

enum class QueueLevel {
    NORMAL, HIGH
}

enum class QueueType {
    ACCEPT, FINISH, FAIL, QUIT, RESET
}

enum class SelectObject {
    SELF {
        override fun objective(player: Player): List<Player> {
            return listOf(player)
        }
         },
    TEAM {
        override fun objective(player: Player): List<Player> {
            return player.teamData()?.playerMembers()?.toMutableList()?: mutableListOf()
        }
         },
    ALL {
        override fun objective(player: Player): List<Player> {
            return Bukkit.getOnlinePlayers().toMutableList()
        }
    };

    abstract fun objective(player: Player): List<Player>
}