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
        val tData = TeamManager.getTeamData(uuid)?: return run { TLocale.sendTo(player, "TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "hasTeamInfo.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        val mem = UtilTeam.getStr(yaml, "hasTeamInfo.members.content", tData, leaderName)
        val memHover = UtilTeam.getStr(yaml, "hasTeamInfo.members.hover", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(mem))
            .hoverText(TLocale.Translate.setColored(memHover))
            .clickCommand("/QuestEngine teamMembers")
            .send(player)

        if (TeamManager.isLeader(uuid, tData)) {
            val asks = UtilTeam.getStr(yaml, "hasTeamInfo.asks.content", tData, leaderName)
            val asksHover = UtilTeam.getStr(yaml, "hasTeamInfo.asks.hover", tData, leaderName)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(asks))
                .hoverText(TLocale.Translate.setColored(asksHover))
                .clickCommand("/QuestEngine teamAsks")
                .send(player)
            val del = UtilTeam.getStr(yaml, "hasTeamInfo.delete.content", tData, leaderName)
            val delHover = UtilTeam.getStr(yaml, "hasTeamInfo.delete.hover", tData, leaderName)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(del))
                .hoverText(TLocale.Translate.setColored(delHover))
                .clickCommand("/QuestEngine teamDelete")
                .send(player)
        }else {
            val left = UtilTeam.getStr(yaml, "hasTeamInfo.leave.content", tData, leaderName)
            val leftHover = UtilTeam.getStr(yaml, "hasTeamInfo.leave.hover", tData, leaderName)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(left))
                .hoverText(TLocale.Translate.setColored(leftHover))
                .clickCommand("/QuestEngine teamLeave")
                .send(player)
        }

        val footer = UtilTeam.getStr(yaml, "hasTeamInfo.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)
    }

    fun openMembers(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return run { TLocale.sendTo(player, "TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "members.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        val memberStr = UtilTeam.getStr(yaml, "members.member.content", tData, leaderName)
        val kickHover = UtilTeam.getStr(yaml, "members.member.hover")
        tData.members.forEach {
            val m = Bukkit.getPlayer(it)?: return@forEach
            val mName = m.name
            val member= TellrawJson.create()
                .append(TLocale.Translate.setColored(memberStr).replace("%memberName%", mName))
            if (TeamManager.isLeader(uuid, tData) && it != uuid) {
                member
                    .hoverText(TLocale.Translate.setColored(kickHover))
                    .clickSuggest("/questengine teamKick $mName")
            }
            member.send(player)
        }

        val footer = UtilTeam.getStr(yaml, "members.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)

    }

    fun openAsks(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return run { TLocale.sendTo(player, "TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "asks.head", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        val infoStr = UtilTeam.getStr(yaml, "asks.info.content")
        val infoHover = UtilTeam.getStr(yaml, "asks.info.hover")
        val agree = UtilTeam.getStr(yaml, "asks.agree.content")
        val agreeHover = UtilTeam.getStr(yaml, "asks.agree.hover")
        val reject = UtilTeam.getStr(yaml, "asks.reject.content")
        val rejectHover = UtilTeam.getStr(yaml, "asks.reject.hover")
        tData.asks.forEach {
            val m = Bukkit.getPlayer(it)?: return@forEach
            val mName = m.name
            TellrawJson.create()
                .append(TLocale.Translate.setColored(infoStr).replace("%memberName%", mName))
                .hoverText(TLocale.Translate.setColored(infoHover))
                .send(player)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(agree).replace("%memberName%", mName))
                .hoverText(TLocale.Translate.setColored(agreeHover))
                .clickSuggest("/questengine teamAskAgree $mName")
                .send(player)
            TellrawJson.create()
                .append(TLocale.Translate.setColored(reject).replace("%memberName%", mName))
                .hoverText(TLocale.Translate.setColored(rejectHover))
                .clickSuggest("/questengine teamAskReject $mName")
                .send(player)
        }

        val footer = UtilTeam.getStr(yaml, "asks.footer", tData, leaderName)
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)

    }

}