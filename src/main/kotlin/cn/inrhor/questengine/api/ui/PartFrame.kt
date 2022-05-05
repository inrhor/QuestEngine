package cn.inrhor.questengine.api.ui

class PartFrame(val id: String, val note: MutableList<String>, val condition: MutableList<String>) {
    constructor(): this("part", mutableListOf(), mutableListOf())
}