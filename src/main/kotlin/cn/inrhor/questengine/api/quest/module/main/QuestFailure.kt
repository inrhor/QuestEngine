package cn.inrhor.questengine.api.quest.module.main

import taboolib.library.configuration.PreserveNotNull

@PreserveNotNull
class QuestFailure(val check: Int, val condition: List<String>, val script: List<String>) {
    constructor(): this(-1, listOf(), listOf())
}