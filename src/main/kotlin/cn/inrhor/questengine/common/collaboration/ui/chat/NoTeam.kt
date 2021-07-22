package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.utlis.file.GetFile
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.entity.Player

object NoTeam {

    fun openHome(player: Player) {

        val uuid = player.uniqueId

        if (TeamManager.hasTeam(uuid)) {
            HasTeam.openInfo(player)
            return
        }

        val yaml = GetFile.yaml("team", "chat")

        val head = UtilTeam.getStr(yaml, "noTeamHome.head")
        TellrawJson.create()
            .append(TLocale.Translate.setColored(head))
            .send(player)

        val create = UtilTeam.getStr(yaml, "noTeamHome.create.content")
        val createHover = UtilTeam.getStr(yaml, "noTeamHome.create.hover")
        TellrawJson.create()
            .append(TLocale.Translate.setColored(create))
            .hoverText(TLocale.Translate.setColored(createHover))
            .clickSuggest("/questengine teamCreate")
            .send(player)

        val join = UtilTeam.getStr(yaml, "noTeamHome.join.content")
        val joinHover = UtilTeam.getStr(yaml, "noTeamHome.join.hover")
        TellrawJson.create()
            .append(TLocale.Translate.setColored(join))
            .hoverText(TLocale.Translate.setColored(joinHover))
            .clickSuggest("/questengine teamJoin")
            .send(player)

        val footer = UtilTeam.getStr(yaml, "noTeamHome.footer")
        TellrawJson.create()
            .append(TLocale.Translate.setColored(footer))
            .send(player)

    }

}