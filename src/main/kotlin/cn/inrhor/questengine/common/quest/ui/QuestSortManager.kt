package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderJsonUI
import cn.inrhor.questengine.utlis.ui.buildJsonUI

/**
 * 任务手册分类
 */
object QuestSortManager {

    /**
     * 分类标签
     * 包含的任务模块内容
     */
    val sortList = mutableMapOf<String, QuestSort>()

    /**
     * JSON界面内容
     */
    val jsonUI = mutableMapOf<String, String>()

    fun init() {
        load()
    }

    fun load() {
        val yaml = releaseFile("handbook/sort.yml", false)
        // 分类界面
        val sortJsonUI = buildJsonUI {
            yamlAddDesc(yaml, "head")
            sectionAdd(yaml, "sort", BuilderJsonUI.Type.SORT)
        }
        jsonUI["sortHome"] = sortJsonUI
    }

    fun reload() {
        jsonUI.clear()
        load()
    }

}