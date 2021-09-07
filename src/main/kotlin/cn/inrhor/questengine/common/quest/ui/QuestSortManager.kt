package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderJsonUI
import cn.inrhor.questengine.utlis.ui.buildJsonUI

/**
 * 任务手册分类
 */
object QuestSortManager {

    /**
     * 分类界面
     */
    var sortHomeUI = ""

    val sortQuest = mutableMapOf<String, MutableSet<QuestModule>>()

    fun addSortQuest(sort: String, questModule: QuestModule) {
        (sortQuest[sort]?: mutableSetOf()).add(questModule)
    }

    fun init() {
        load()
    }

    fun load() {
        val yaml = releaseFile("handbook/sort.yml", false)
        // 分类界面
        val sortUI = buildJsonUI {
            yamlAddDesc(yaml, "head")
            sectionAdd(yaml, "sort", BuilderJsonUI.Type.SORT)
        }
        sortHomeUI = sortUI.build().toRawMessage()
    }

}