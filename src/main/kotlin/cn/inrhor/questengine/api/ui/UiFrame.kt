package cn.inrhor.questengine.api.ui

/**
 * 实例Json框架
 */
class UiFrame(var head: MutableList<String>, val fork: MutableList<String>, val part: MutableList<PartFrame>, val addon: MutableList<AddonFrame>){
    constructor(): this(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
}