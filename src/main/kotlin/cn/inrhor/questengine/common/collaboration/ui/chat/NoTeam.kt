package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.entity.Player
import taboolib.common.platform.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored

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
            .append(head.colored())
            .sendTo(adaptPlayer(player))

        val create = UtilTeam.getStr(yaml, "noTeamHome.create.content")
        val createHover = UtilTeam.getStr(yaml, "noTeamHome.create.hover")
        TellrawJson()
            .append(create.colored())
            .hoverText(createHover.colored())
            .suggestCommand("/questengine team create")
            .sendTo(adaptPlayer(player))

        val join = UtilTeam.getStr(yaml, "noTeamHome.join.content")
        val joinHover = UtilTeam.getStr(yaml, "noTeamHome.join.hover")
        TellrawJson()
            .append(join.colored())
            .hoverText(joinHover.colored())
            .suggestCommand("/questengine team join")
            .sendTo(adaptPlayer(player))

        val footer = UtilTeam.getStr(yaml, "noTeamHome.footer")
        TellrawJson()
            .append(footer.colored())
            .sendTo(adaptPlayer(player))

    }

}