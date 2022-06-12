package cn.inrhor.questengine.api.ui

class PartFrame(val id: String, val note: List<String>, val condition: String) {
    constructor(): this("part", listOf(), "")
}