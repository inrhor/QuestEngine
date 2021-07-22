package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.utlis.file.GetFile
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object HasTeam {

    fun openInfo(player: Player) {

        val uuid = player.uniqueId
        val pData = DataStorage.getPlayerData(uuid)?: return
        val tData = pData.teamData?: return

        val yaml = GetFile.yaml("team", "chat")

        var leaderName = player.name
        if (tData.leader != uuid) {
            val leader = Bukkit.getPlayer(tData.leader)?: return
            leaderName = leader.name
        }

        val head = getStr(yaml, "hasTeamInfo.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        /*tData.members.forEach {
            val m = Bukkit.getPlayer(it)?: return@forEach
            TellrawJson.create()
                .append(TLocale.Translate.setColored("   &r"+m.name+"  \n test"))
                .send(player)
        }*/

        if (TeamManager.isLeader(uuid, tData)) {
            val del = getStr(yaml, "hasTeamInfo.delete.content", tData, leaderName)
            val delHover = getStr(yaml, "hasTeamInfo.delete.hover", tData, leaderName)
            val delete = TellrawJson.create()
                .append(TLocale.Translate.setColored(del))
                .hoverText(TLocale.Translate.setColored(delHover))
                .clickCommand("/QuestEngine teamDelete")
            delete.send(player)
        }else {
            val left = getStr(yaml, "hasTeamInfo.leave.content", tData, leaderName)
            val leftHover = getStr(yaml, "hasTeamInfo.leave.hover", tData, leaderName)
            val leave = TellrawJson.create()
                .append(TLocale.Translate.setColored(left))
                .hoverText(TLocale.Translate.setColored(leftHover))
                .clickCommand("/QuestEngine teamLeave")
            leave.send(player)
        }

        val footer = getStr(yaml, "hasTeamInfo.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)
    }

    private fun getStr(yaml: YamlConfiguration, node: String, tData: TeamOpen, leaderName: String): String {
        var content = ""
        yaml.getStringList(node).forEach {
            content = "$content&r\n$it"
        }
        return content.replace("%teamName%", tData.teamName, true)
            .replace("%teamAmount%", tData.members.size.toString(), true)
            .replace("%leaderName%", leaderName, true)
    }

}