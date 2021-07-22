package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.api.collaboration.TeamOpen
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object UtilTeam {

    fun getStr(yaml: YamlConfiguration, node: String, tData: TeamOpen, leaderName: String): String {
        var content = ""
        yaml.getStringList(node).forEach {
            content = "$content&r\n$it"
        }
        return content.replace("%teamName%", tData.teamName, true)
            .replace("%teamAmount%", tData.members.size.toString(), true)
            .replace("%leaderName%", leaderName, true)
    }

    fun getStr(yaml: YamlConfiguration, node: String): String {
        var content = ""
        yaml.getStringList(node).forEach {
            content = "$content&r\n$it"
        }
        return content
    }

    fun leaderName(player: Player, tData: TeamOpen): String {
        var leaderName = player.name
        if (tData.leader != player.uniqueId) {
            val leader = Bukkit.getPlayer(tData.leader)?: return ""
            leaderName = leader.name
        }
        return leaderName
    }

}