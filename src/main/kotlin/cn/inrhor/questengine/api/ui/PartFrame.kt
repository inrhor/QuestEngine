package cn.inrhor.questengine.api.ui

class PartFrame(val id: String, val note: List<String>, val condition: List<String>) {
    constructor(): this("part", listOf(), listOf())
}