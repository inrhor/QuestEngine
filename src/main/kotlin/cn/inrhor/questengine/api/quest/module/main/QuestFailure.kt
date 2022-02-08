package cn.inrhor.questengine.api.quest.module.main

import taboolib.library.configuration.PreserveNotNull

@PreserveNotNull
class QuestFailure(var check: Int, var condition: MutableList<String>, var script: MutableList<String>) {
    constructor(): this(-1, mutableListOf(), mutableListOf())
}