package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.utlis.file.GetFile
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object HasTeam {

    fun openInfo(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "hasTeamInfo.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        if (TeamManager.isLeader(uuid, tData)) {
            val del = UtilTeam.getStr(yaml, "hasTeamInfo.delete.content", tData, leaderName)
            val delHover = UtilTeam.getStr(yaml, "hasTeamInfo.delete.hover", tData, leaderName)
            val delete = TellrawJson.create()
                .append(TLocale.Translate.setColored(del))
                .hoverText(TLocale.Translate.setColored(delHover))
                .clickCommand("/QuestEngine teamDelete")
            delete.send(player)
        }else {
            val left = UtilTeam.getStr(yaml, "hasTeamInfo.leave.content", tData, leaderName)
            val leftHover = UtilTeam.getStr(yaml, "hasTeamInfo.leave.hover", tData, leaderName)
            val leave = TellrawJson.create()
                .append(TLocale.Translate.setColored(left))
                .hoverText(TLocale.Translate.setColored(leftHover))
                .clickCommand("/QuestEngine teamLeave")
            leave.send(player)
        }
        val mem = UtilTeam.getStr(yaml, "hasTeamInfo.members.content", tData, leaderName)
        val memHover = UtilTeam.getStr(yaml, "hasTeamInfo.members.hover", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(mem))
            .hoverText(TLocale.Translate.setColored(memHover))
            .clickCommand("/QuestEngine teamMembers")
            .send(player)

        val footer = UtilTeam.getStr(yaml, "hasTeamInfo.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)
    }

    fun openMembers(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "members.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        tData.members.forEach {
            val memberStr = UtilTeam.getStr(yaml, "members.member.content", tData, leaderName)
            val m = Bukkit.getPlayer(it)?: return@forEach
            val mName = m.name
            val member= TellrawJson.create()
                .append(TLocale.Translate.setColored(memberStr).replace("%memberName%", mName))
            if (TeamManager.isLeader(uuid, tData) && it != uuid) {
                val kickHover = UtilTeam.getStr(yaml, "members.member.hover", tData, leaderName, mName)
                member
                    .hoverText(kickHover)
                    .clickSuggest("/questengine teamKick $mName")
            }
            member.send(player)
        }

        val footer = UtilTeam.getStr(yaml, "members.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)

    }

}