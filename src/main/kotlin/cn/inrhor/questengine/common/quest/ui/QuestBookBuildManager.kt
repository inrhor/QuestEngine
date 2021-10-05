package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.copy
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderFrame
import cn.inrhor.questengine.utlis.ui.NoteComponent
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildFrame
import org.bukkit.entity.Player
import taboolib.module.chat.TellrawJson

/**
 * 任务手册构建工具
 */
object QuestBookBuildManager {

    /**
     * 分类界面
     */
    var sortHomeUI = mutableListOf<TellrawJson>()

    val sortViewUI = buildFrame()

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
        val sortUI = buildFrame {
            yamlAddNote(sort, "head")
            sectionAdd(sort, "sort", BuilderFrame.Type.SORT)
        }
        sortHomeUI = sortUI.build()

        val sortView = releaseFile("handbook/sortView.yml", false)
        sortViewUI.yamlAddNote(sortView, "head")
        sortViewUI.yamlAutoAdd(sortView, BuilderFrame.Type.CUSTOM, "for")
    }

    private fun getTextComp(id: String): TextComponent? {
        return sortViewUI.textComponent[id]
    }

    /**
     * 为用户编译任务手册的任务分类信息
     */
    fun questSortBuild(player: Player, sort: String): MutableList<TellrawJson> {
        val pData = DataStorage.getPlayerData(player)
        val qData = pData.questDataList
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewUI.copy()
        val textCompNo = getTextComp("for.noClick")?: return mutableListOf()
        val textCompClick = getTextComp("for.click")?: return mutableListOf()
        sortView.textComponent.clear()

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
            if (!hasDisplay.contains(id)) {
                val noText = textCompNo.copy()
                val clickText = textCompClick.copy()
                setText(player, id,  sortView, noText)
                setText(player, id, sortView, clickText)
            }
        }

        return sortView.build(player)
    }

    private fun setText(player: Player, questID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        if (builderFrame.textComponent.containsKey(questID)) return
        val fork = builderFrame.noteComponent["for.fork"]?: return
        builderFrame.noteComponent[questID] = NoteComponent(fork.note.copy(), fork.condition(player).copy())

        if (!builderFrame.textCondition(player, listReply(player, questID, textComponent.condition))) return

        val qModule = QuestManager.getQuestModule(questID)?: return
        val qDesc = "#quest-desc-info"
        val hover = textComponent.hover
        if (hover.contains(qDesc)) {
            val desc = qModule.descMap["info"]
            if (desc != null) {
                textComponent.hover = desc
            }
        }

        textComponent.command = "/qen handbook info $questID"

        builderFrame.noteComponent.values.forEach {
            if (!it.fork) {
                it.note = listReply(player, questID, it.note(player))
                it.condition = listReply(player, questID, it.condition(player))
            }
        }

        builderFrame.textComponent[questID] = textComponent
    }

    private fun accept(player: Player, questID: String): Boolean {
        return QuestManager.existQuestData(player.uniqueId, questID)
    }

    fun listReply(player: Player, questID: String, list: MutableList<String>): MutableList<String> {
        for (i in 0 until list.size) {
            val it = list[i]
            val qModule = QuestManager.getQuestModule(questID)?: break
            list[i] = list[i].replace("#quest-name", qModule.name, true)
            when {
                it == "#!quest-accept" -> {
                    list[i] = "type " + !accept(player, questID)
                }
                it == "#quest-accept" -> {
                    list[i] = "type " + accept(player, questID)
                }
                list[i].lowercase().contains("#quest-id") -> {
                    list[i] = list[i].replace("#quest-id", questID, true)
                }
            }
        }
        return list
    }

}