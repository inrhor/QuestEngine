package cn.inrhor.questengine.api.ui

/**
 * 实例Json框架
 */
class UiFrame(var head: List<String>, val fork: List<String>, val part: MutableList<PartFrame>, val addon: MutableList<AddonFrame>){
    constructor(): this(listOf(), listOf(), mutableListOf(), mutableListOf())
}