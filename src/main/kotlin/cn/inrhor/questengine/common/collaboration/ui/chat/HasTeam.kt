package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.chat.HexColor
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*

object HasTeam {

    fun openInfo(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return run { player.sendLang("TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "hasTeamInfo.head", tData, leaderName)
        TellrawJson()
            .append(head.colored())
            .sendTo(adaptPlayer(player))

        val mem = UtilTeam.getStr(yaml, "hasTeamInfo.members.content", tData, leaderName)
        val memHover = UtilTeam.getStr(yaml, "hasTeamInfo.members.hover", tData, leaderName)
        TellrawJson()
            .append(mem.colored())
            .hoverText(memHover.colored())
            .runCommand("/QuestEngine teamMembers")
            .sendTo(adaptPlayer(player))

        if (TeamManager.isLeader(uuid, tData)) {
            val asks = UtilTeam.getStr(yaml, "hasTeamInfo.asks.content", tData, leaderName)
            val asksHover = UtilTeam.getStr(yaml, "hasTeamInfo.asks.hover", tData, leaderName)
            TellrawJson()
                .append(asks.colored())
                .hoverText(asksHover.colored())
                .runCommand("/QuestEngine teamAsks")
                .sendTo(adaptPlayer(player))
            val del = UtilTeam.getStr(yaml, "hasTeamInfo.delete.content", tData, leaderName)
            val delHover = UtilTeam.getStr(yaml, "hasTeamInfo.delete.hover", tData, leaderName)
            TellrawJson()
                .append(del.colored())
                .hoverText(delHover.colored())
                .runCommand("/QuestEngine teamDelete")
                .sendTo(adaptPlayer(player))
        }else {
            val left = UtilTeam.getStr(yaml, "hasTeamInfo.leave.content", tData, leaderName)
            val leftHover = UtilTeam.getStr(yaml, "hasTeamInfo.leave.hover", tData, leaderName)
            TellrawJson()
                .append(left.colored())
                .hoverText(leftHover.colored())
                .runCommand("/QuestEngine teamLeave")
                .sendTo(adaptPlayer(player))
        }

        val footer = UtilTeam.getStr(yaml, "hasTeamInfo.footer", tData, leaderName)
        TellrawJson()
            .append(footer.colored())
            .sendTo(adaptPlayer(player))
    }

    fun openMembers(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return run { player.sendLang("TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "members.head", tData, leaderName)
        TellrawJson()
            .append(head.colored())
            .sendTo(adaptPlayer(player))

        val memberStr = UtilTeam.getStr(yaml, "members.member.content", tData, leaderName)
        val kickHover = UtilTeam.getStr(yaml, "members.member.hover")
        tData.members.forEach {
            val m = Bukkit.getPlayer(it)?: return@forEach
            val mName = m.name
            val member= TellrawJson()
                .append(memberStr.replace("%memberName%", mName).colored())
            if (TeamManager.isLeader(uuid, tData) && it != uuid) {
                member
                    .hoverText(kickHover.colored())
                    .suggestCommand("/questengine teamKick $mName")
            }
            member.sendTo(adaptPlayer(player))
        }

        val footer = UtilTeam.getStr(yaml, "members.footer", tData, leaderName)
        TellrawJson()
            .append(footer.colored())
            .sendTo(adaptPlayer(player))

    }

    fun openAsks(player: Player) {

        val uuid = player.uniqueId
        val tData = TeamManager.getTeamData(uuid)?: return run { player.sendLang("TEAM.NO_TEAM") }

        val yaml = GetFile.yaml("team", "chat")

        val leaderName = UtilTeam.leaderName(player, tData)

        val head = UtilTeam.getStr(yaml, "asks.head", tData, leaderName)
        TellrawJson()
            .append(head.colored())
            .sendTo(adaptPlayer(player))

        val infoStr = UtilTeam.getStr(yaml, "asks.info.content")
        val infoHover = UtilTeam.getStr(yaml, "asks.info.hover")
        val agree = UtilTeam.getStr(yaml, "asks.agree.content")
        val agreeHover = UtilTeam.getStr(yaml, "asks.agree.hover")
        val reject = UtilTeam.getStr(yaml, "asks.reject.content")
        val rejectHover = UtilTeam.getStr(yaml, "asks.reject.hover")
        tData.asks.forEach {
            val m = Bukkit.getPlayer(it)?: return@forEach
            val mName = m.name
            TellrawJson()
                .append(infoStr.replace("%memberName%", mName).colored())
                .hoverText(infoHover.colored())
                .sendTo(adaptPlayer(player))
            TellrawJson()
                .append(agree.replace("%memberName%", mName).colored())
                .hoverText(HexColor.translate(agreeHover))
                .suggestCommand("/questengine teamAskAgree $mName")
                .sendTo(adaptPlayer(player))
            TellrawJson()
                .append(reject.replace("%memberName%", mName).colored())
                .hoverText(HexColor.translate(rejectHover))
                .suggestCommand("/questengine teamAskReject $mName")
                .sendTo(adaptPlayer(player))
        }

        val footer = UtilTeam.getStr(yaml, "asks.footer", tData, leaderName)
        TellrawJson()
            .append(footer.colored())
            .sendTo(adaptPlayer(player))

    }

}