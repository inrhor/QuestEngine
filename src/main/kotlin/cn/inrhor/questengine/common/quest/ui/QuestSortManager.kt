package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildJsonUI
import cn.inrhor.questengine.utlis.ui.textComponent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

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
     * 分类标签包含的JSON内容
     */
    val sortJson = mutableMapOf<String, String>()


    @Awake(LifeCycle.ACTIVE)
    fun init() {
        load()
    }

    fun load() {
        val yaml = releaseFile("quest/sort.yml")
        // 分类界面
        val sortJsonUI = buildJsonUI {
            yamlAddDesc(yaml, "head")
            sectionAdd(yaml, "sort")
        }
        sortJson["home"] = sortJsonUI
    }

    fun reload() {

    }

}