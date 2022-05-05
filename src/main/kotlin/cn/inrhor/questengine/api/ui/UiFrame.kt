package cn.inrhor.questengine.api.ui

/**
 * 实例Json框架
 */
class UiFrame(var head: List<String>, val fork: List<String>, val part: List<PartFrame>, val addon: List<AddonFrame>){
    constructor(): this(listOf(), listOf(), listOf(), listOf())
}