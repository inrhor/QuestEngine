package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule


/**
 * 任务模块
 *
 * @param name 任务名称
 * @param startInnerQuestID 开始的内部任务ID
 * @param sort 任务手册分类
 */
class QuestModule(
    var questID: String,
    var name: String,
    var startInnerQuestID: String,
    val mode: QuestMode,
    val accept: QuestAccept,
    val failure: QuestFailure,
    var sort: String) {

    constructor(): this("null", "nullName", "", QuestMode(), QuestAccept(), QuestFailure(),  "")

    @Transient var innerQuestList: MutableList<QuestInnerModule> = mutableListOf()
    @Transient var descMap: MutableMap<String, List<String>> = mutableMapOf()

    fun getStartInnerQuest(): QuestInnerModule? {
        innerQuestList.forEach {
            if (it.id == startInnerQuestID) return it
        }
        return null
    }

}
