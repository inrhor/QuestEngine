package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderJsonUI
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildJsonUI
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

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
        if (sortQuest.containsKey(sort)) {
            sortQuest[sort]!!.add(questModule)
            return
        }
        sortQuest[sort] = mutableSetOf(questModule)
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
        sortViewUI.clear()
        sortViewUI.yamlAddDesc(sortView, "head")
        sortViewUI.yamlAdd(sortView, BuilderJsonUI.Type.CUSTOM, "for")
    }

    private fun getTextComp(id: String): TextComponent? {
        return sortViewUI.textComponentMap[id]
    }

    /**
     * 为用户编译任务手册的任务分类信息
     */
    fun questSortBuild(player: Player, sort: String): String {
        val pData = DataStorage.getPlayerData(player)
        val qData = pData.questDataList
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewUI.copy()
        val textCompNo = getTextComp("for.noClick")?: return ""
        val textCompClick = getTextComp("for.click")?: return ""
        sortView.textComponentMap.clear()

        qData.values.forEach {
            val id = it.questID
            val m = QuestManager.getQuestModule(id)
            if (m?.sort == sort && it.state != QuestState.FINISH && !hasDisplay.contains(id)) {
                hasDisplay.add(id)
                val textComp = textCompClick.copy()
                setText(player, id, sortView, textComp)
            }
        }

        val sortList = sortQuest[sort]
        sortList?.forEach {
            val id = it.questID
            info("qen $id  "+hasDisplay.contains(id))
            if (!hasDisplay.contains(id)) {
                val noText = textCompNo.copy()
                val clickText = textCompClick.copy()
                setText(player, id,  sortView, noText)
                setText(player, id, sortView, clickText)
//                sortView.textComponentMap["for.click"] = noText
            }
        }

        return sortView.build(player).toRawMessage()
    }

    private fun setText(player: Player, questID: String, builderJsonUI: BuilderJsonUI, textComponent: TextComponent) {
        val c = textComponent.condition
        for (i in 0 until c.size) {
            val it = c[i]
            if (it == "#!quest-accept") {
                c[i] = "type "+!accept(player, questID)
            }else if (it == "#quest-accept") {
                c[i] = "type "+accept(player, questID)
            }
        }

        val qModule = QuestManager.getQuestModule(questID)?: return
        val qDesc = "#quest-desc-info"
        val hover = textComponent.hover
        if (hover.contains(qDesc)) {
            val desc = qModule.descMap["info"]
            if (desc != null) {
                textComponent.hover = desc
            }
        }

        val qName = "#quest-name"
        val qID = "#quest-sb"
        val d = builderJsonUI.description
        for (s in 0 until d.size) {
            d[s] = d[s].replace(qName, qModule.name, true)
        }
        for (s in 0 until d.size) {
            val u = d[s]
            if (u.lowercase().contains(qID)) {
                d[s] = d[s].replace(qID, questID, true)
                break
            }
        }

        builderJsonUI.textComponentMap[questID] = textComponent
    }

    private fun accept(player: Player, questID: String): Boolean {
        return QuestManager.existQuestData(player.uniqueId, questID)
    }

}