package cn.inrhor.questengine.api.ui

class AddonFrame(val id: String, val text: List<String>, val condition: List<String>, val hover: List<String>, val command: String) {
    constructor(): this("addon", listOf(), listOf(), listOf(), "")
}