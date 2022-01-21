package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
import taboolib.library.configuration.PreserveNotNull

/**
 * 任务模块
 *
 * @param name 任务名称
 * @param startInnerQuestID 开始的内部任务ID
 * @param innerQuestList 内部任务集
 * @param sort 任务手册分类
 */
class QuestModule(
    @PreserveNotNull val questID: String = "", @PreserveNotNull val name: String = "null name", @PreserveNotNull val startInnerQuestID: String = "",
    @PreserveNotNull val mode: QuestMode = QuestMode(),
    @PreserveNotNull val accept: QuestAccept = QuestAccept(), @PreserveNotNull val failure: QuestFailure = QuestFailure(),
    @PreserveNotNull var innerQuestList: MutableList<QuestInnerModule> = mutableListOf(),
    @PreserveNotNull val sort: String = "",
    @Transient var descMap: MutableMap<String, List<String>> = mutableMapOf()) {

    fun getStartInnerQuest(): QuestInnerModule? {
        innerQuestList.forEach {
            if (it.id == startInnerQuestID) return it
        }
        return null
    }

}