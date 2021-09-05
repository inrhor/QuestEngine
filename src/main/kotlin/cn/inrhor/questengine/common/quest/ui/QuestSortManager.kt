package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.buildJsonUI
import taboolib.module.chat.TellrawJson

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
        val yaml = releaseFile("space/quest/sort.yml", false)
        // 分类界面
        val sortJsonUI = buildJsonUI {
            yamlAddDesc(yaml, "head")
            sectionAdd(yaml, "sort")
        }
        jsonUI["home"] = sortJsonUI
    }

    fun reload() {
        jsonUI.clear()
        load()
    }

}