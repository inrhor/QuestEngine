package cn.inrhor.questengine.api.quest.module.main

import org.bukkit.entity.Player

import taboolib.platform.util.asLangText


class QuestAccept(var way: String, var maxQuantity: Int, var check: Int, var condition: MutableList<String>) {
    constructor(): this("", 1, -1, mutableListOf())

    fun wayLang(player: Player) =
        if (way == "auto") player.asLangText("QUEST-ACCEPT-WAY-AUTO")
        else player.asLangText("QUEST-ACCEPT-WAY-COMMON")
}