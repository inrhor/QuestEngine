package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.QuestInnerData
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.QuestTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.copy
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.time.TimeUtil
import cn.inrhor.questengine.utlis.ui.BuilderFrame
import cn.inrhor.questengine.utlis.ui.NoteComponent
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildFrame
import org.bukkit.entity.Player
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.TellrawJson
import java.util.*

/**
 * 任务手册构建工具
 */
object QuestBookBuildManager {

    /**
     * 分类界面
     */
    var sortHomeUI = mutableListOf<TellrawJson>()

    /**
     * 分类中任务列表
     */
    val sortViewUI = buildFrame()

    /**
     * 分类任务模块列表
     */
    val sortQuest = mutableMapOf<String, MutableSet<QuestModule>>()

    /**
     * 任务信息手册
     */
    val questNoteUI = buildFrame()

    /**
     * 内部任务信息手册
     */
    val innerQuestNoteUI = buildFrame()

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
        val sort = releaseFile("ui/handbook/sort.yml", false)
        // 分类界面
        val sortUI = buildFrame {
            yamlAddNote(sort, "head")
            sectionAdd(sort, "sort", BuilderFrame.Type.SORT)
        }
        sortHomeUI = sortUI.build()

        val sortView = releaseFile("ui/handbook/sortView.yml", false)
        sortViewUI.yamlAddNote(sortView, "head")
        sortViewUI.yamlAutoAdd(sortView, BuilderFrame.Type.CUSTOM, "for")

        val qNoteYaml = releaseFile("ui/handbook/questNote.yml", false)
        questNoteUI.yamlAddNote(qNoteYaml, "head")
        questNoteUI.sectionAdd(qNoteYaml, "already", BuilderFrame.Type.CUSTOM)

        val iNoteYaml = releaseFile("ui/handbook/innerQuestNote.yml", false)
        innerQuestNoteUI.sectionAdd(iNoteYaml, "inner", BuilderFrame.Type.CUSTOM)
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

    fun questNoteBuild(player: Player, questID: String): MutableList<TellrawJson> {
        val ui = questNoteUI.copy()
        ui.noteComponent.values.forEach {
            it.note = listReply(player, questID, it.note)
            it.condition = listReply(player, questID, it.condition)
        }
        return ui.build(player)
    }

    fun innerQuestNoteBuild(player: Player, questID: String, innerID: String): MutableList<TellrawJson> {
        val ui = innerQuestNoteUI.copy()
        ui.noteComponent.values.forEach {
            it.note = listReply(player, questID, it.note, innerID)
            it.note = descSet(it.note, "", questID, innerID)
            it.condition = listReply(player, questID, it.condition, innerID)
        }
        return ui.build(player)
    }

    fun targetNodeBuild(player: Player, questUUID: UUID, innerID: String): MutableList<TellrawJson> {
        val list = mutableListOf<TellrawJson>()
        val innerData = QuestManager.getInnerQuestData(player, questUUID, innerID)?: return list
        innerData.targetsData.values.forEach {
            allTargetNoteBuild(player, innerData, it.questTarget).forEach { t ->
                list.add(t)
            }
        }
        return list
    }

    private fun allTargetNoteBuild(player: Player, innerData: QuestInnerData, target: QuestTarget): MutableList<TellrawJson> {
        val tData = innerData.targetsData[target.name]?: return mutableListOf()
        var time = "null"
        val endDate = tData.endTimeDate
        if (endDate != null) {
            time = TimeUtil.remainDate(player, innerData.state, endDate)
        }
        val targetUI = buildFrame() {
            noteComponent = target.noteMap.toMutableMap()
            noteComponent.values.forEach {
                it.note.forEach { s ->
                    s.replaceWithOrder(time, tData.schedule)
                }
            }
        }
        return targetUI.build(player)
    }

    private fun setText(player: Player, questID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        if (builderFrame.textComponent.containsKey(questID)) return
        val fork = builderFrame.noteComponent["for.fork"]?: return
        builderFrame.noteComponent[questID] = NoteComponent(fork.note.copy(), fork.condition(player).copy())

        if (!builderFrame.textCondition(player, listReply(player, questID, textComponent.condition))) return

        textComponent.hover = descSet(textComponent.hover, "info", questID)

        textComponent.command = "/qen handbook info $questID"

        builderFrame.noteComponent.values.forEach {
            if (!it.fork) {
                it.note = listReply(player, questID, it.note(player))
                it.condition = listReply(player, questID, it.condition(player))
            }
        }

        builderFrame.textComponent[questID] = textComponent
    }

    fun descSet(list: MutableList<String>, sign: String, questID: String, innerID: String = ""): MutableList<String> {
        val desc = mutableListOf<String>()

        for (i in 0 until list.size) {
            if ((sign.isNotEmpty() && list[i] == "#quest-desc-$sign") || list[i] == "#innerQuest-description") {
                getDescMap(questID, innerID, sign)?.forEach { e ->
                    desc.add(e)
                }
            }else {
                desc.add(list[i])
            }
        }
        return desc
    }

    private fun getDescMap(questID: String, innerID: String, sign: String): MutableList<String>? {
        if (innerID.isNotEmpty()) {
            val innerModule = QuestManager.getInnerQuestModule(questID, innerID)?: return null
            return innerModule.description
        }
        val qModule = QuestManager.getQuestModule(questID)?: return null
        return qModule.descMap[sign]
    }

    private fun accept(player: Player, questID: String): Boolean {
        return QuestManager.existQuestData(player.uniqueId, questID)
    }

    private fun finish(player: Player, questID: String): Boolean {
        val qData = QuestManager.getQuestData(player.uniqueId, questID)?: return false
        return qData.state == QuestState.FINISH
    }

    fun listReply(player: Player, questID: String, list: MutableList<String>, innerID: String = ""): MutableList<String> {
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
                it == "#!quest-finish" -> {
                    list[i] = "type " + !finish(player, questID)
                }
                it == "#quest-finish" -> {
                    list[i] = "type " + finish(player, questID)
                }
                list[i].lowercase().contains("#quest-id") -> {
                    list[i] = list[i].replace("#quest-id", questID, true)
                }
                list[i].lowercase().contains("#innerquest-id") -> {
                    list[i] = list[i].replace("#innerquest-id", innerID, true)
                }
            }
        }
        return list
    }

}