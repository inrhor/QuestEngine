package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.buildUI
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * 任务手册分类
 */
object QuestSort {

    /**
     * 分类标签
     */
//    val sortList = mutableMapOf<String, QuestModule>()


    @Awake(LifeCycle.ACTIVE)
    fun init() {
        load()
    }

    fun load() {
        val yaml = releaseFile("quest/sort.yml")

    }

    fun reload() {

    }

}