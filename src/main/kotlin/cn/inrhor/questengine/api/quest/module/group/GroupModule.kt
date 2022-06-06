package cn.inrhor.questengine.api.quest.module.group

import cn.inrhor.questengine.api.quest.module.inner.QuestModule


/**
 * 任务模块
 *
 * @param name 任务名称
 * @param startQuestID 开始的内部任务ID
 * @param sort 任务手册分类
 */
class GroupModule(
    var questID: String,
    var name: String,
    var startQuestID: String,
    val mode: GroupMode,
    val accept: GroupAccept,
    val failure: GroupFailure,
    var sort: String) {

    constructor(): this("null", "nullName", "", GroupMode(), GroupAccept(), GroupFailure(),  "")

    @Transient var questList: MutableList<QuestModule> = mutableListOf()
    @Transient var descMap: MutableMap<String, List<String>> = mutableMapOf()

    fun getStartInnerQuest(): QuestModule? {
        questList.forEach {
            if (it.id == startQuestID) return it
        }
        return null
    }

    fun existInner(innerID: String): Boolean {
        questList.forEach {
            if (it.id == innerID) return true
        }
        return false
    }

}
