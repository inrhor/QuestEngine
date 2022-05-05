package cn.inrhor.questengine.api.ui

class AddonFrame(val id: String, val text: MutableList<String>, val condition: MutableList<String>, val hover: MutableList<String>, val command: String) {
    constructor(): this("addon", mutableListOf(), mutableListOf(), mutableListOf(), "")
}