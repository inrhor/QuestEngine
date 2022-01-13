package cn.inrhor.questengine.api.quest.module.main

import taboolib.library.configuration.PreserveNotNull

@PreserveNotNull
class QuestAccept(val way: String, val maxQuantity: Int, val check: Int, val condition: List<String>) {
    constructor(): this("auto", 1, -1, listOf())
}