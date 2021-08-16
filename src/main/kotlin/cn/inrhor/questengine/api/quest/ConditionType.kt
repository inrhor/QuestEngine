package cn.inrhor.questengine.api.quest

/**
 * 任务目标条件检查
 */
open class ConditionType(open val content: String, open val contentList: MutableList<String>) {

    constructor(content: String): this(content, mutableListOf())
    constructor(contentList: MutableList<String>): this("", contentList)

    open fun check(): Boolean {
        return false
    }

}