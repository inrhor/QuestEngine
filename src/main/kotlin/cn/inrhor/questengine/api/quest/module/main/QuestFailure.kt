package cn.inrhor.questengine.api.quest.module.main

class QuestFailure(var check: Int, var condition: List<String>, var script: List<String>) {
    constructor(): this(-1, listOf(), listOf())
}