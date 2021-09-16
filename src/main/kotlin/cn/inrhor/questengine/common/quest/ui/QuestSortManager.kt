package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderJsonUI
import cn.inrhor.questengine.utlis.ui.buildJsonUI
import org.bukkit.entity.Player

/**
 * 任务手册分类
 */
object QuestSortManager {

    /**
     * 分类界面
     */
    var sortHomeUI = ""

    val sortViewUI = buildJsonUI()

    /**
     * 分类任务模块列表
     */
    val sortQuest = mutableMapOf<String, MutableSet<QuestModule>>()

    fun addSortQuest(sort: String, questModule: QuestModule) {
        (sortQuest[sort]?: mutableSetOf()).add(questModule)
    }

    fun init() {
        load()
    }

    fun load() {
        val sort = releaseFile("handbook/sort.yml", false)
        // 分类界面
        val sortUI = buildJsonUI {
            yamlAddDesc(sort, "head")
            sectionAdd(sort, "sort", BuilderJsonUI.Type.SORT)
        }
        sortHomeUI = sortUI.build().toRawMessage()

        val sortView = releaseFile("handbook/sortView.yml", false)
        sortViewUI.yamlAddDesc(sortView, "head")
        sortViewUI.yamlAdd(sortView, "for", BuilderJsonUI.Type.CUSTOM)
    }

    /**
     * 为用户编译任务手册的任务分类信息
     */
    fun questSortBuild(player: Player, sort: String): String {
        val pData = DataStorage.getPlayerData(player)
        val qData = pData.questDataList
        val hasDisplay = mutableSetOf<String>()
        qData.values.forEach {
            val id = it.questID
            val m = QuestManager.getQuestModule(id)
            if (m?.sort == sort && it.state != QuestState.FINISH && !hasDisplay.contains(id)) {
                hasDisplay.add(id)

            }
        }
        val sortList = sortQuest[sort]
        sortList?.forEach {
            val id = it.questID
            if (!hasDisplay.contains(id)) {

            }
        }

        return
    }

}