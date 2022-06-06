package cn.inrhor.questengine.api.quest.module.group

import cn.inrhor.questengine.utlis.removeAt
import org.bukkit.entity.Player

import taboolib.platform.util.asLangText


class GroupAccept(var way: String, var maxQuantity: Int, var check: Int, var condition: String) {
    constructor(): this("", 1, -1, "")

    fun delCondition(int: Int) {
        condition = condition.removeAt(int)
    }

    fun wayLang(player: Player) =
        if (way == "auto") player.asLangText("QUEST-ACCEPT-WAY-AUTO")
        else player.asLangText("QUEST-ACCEPT-WAY-COMMON")
}