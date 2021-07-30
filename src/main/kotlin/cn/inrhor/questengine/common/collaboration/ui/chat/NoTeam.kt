package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.chat.HexColor
import taboolib.module.chat.TellrawJson

object NoTeam {

    fun openHome(player: Player) {

        val uuid = player.uniqueId

        if (TeamManager.hasTeam(uuid)) {
            HasTeam.openInfo(player)
            return
        }

        val yaml = GetFile.yaml("team", "chat")

        val head = UtilTeam.getStr(yaml, "noTeamHome.head")
        TellrawJson()
            .append(HexColor.translate(head))
            .sendTo(adaptPlayer(player))

        val create = UtilTeam.getStr(yaml, "noTeamHome.create.content")
        val createHover = UtilTeam.getStr(yaml, "noTeamHome.create.hover")
        TellrawJson()
            .append(HexColor.translate(create))
            .hoverText(HexColor.translate(createHover))
            .suggestCommand("/questengine teamCreate")
            .sendTo(adaptPlayer(player))

        val join = UtilTeam.getStr(yaml, "noTeamHome.join.content")
        val joinHover = UtilTeam.getStr(yaml, "noTeamHome.join.hover")
        TellrawJson()
            .append(HexColor.translate(join))
            .hoverText(HexColor.translate(joinHover))
            .suggestCommand("/questengine teamJoin")
            .sendTo(adaptPlayer(player))

        val footer = UtilTeam.getStr(yaml, "noTeamHome.footer")
        TellrawJson()
            .append(HexColor.translate(footer))
            .sendTo(adaptPlayer(player))

    }

}