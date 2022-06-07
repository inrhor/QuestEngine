package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.api.quest.module.QuestTarget
import cn.inrhor.questengine.api.quest.module.group.GroupModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.enum.toUnit
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
import taboolib.module.configuration.Configuration.Companion.getObject
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
    val sortViewQuestUI = buildFrame()

    /**
     * 内部任务列表
     */
    val innerQuestListUI = buildFrame()

    /**
     * 分类任务模块列表
     */
    val sortQuest = mutableMapOf<String, MutableSet<GroupModule>>()

    /**
     * 任务信息手册
     */
    val questNoteUI = buildFrame()

    /**
     * 内部任务信息手册
     */
    val innerQuestNoteUI = buildFrame()

    fun addSortQuest(sort: String, questModule: GroupModule) {
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
            loadFrame(sort.getObject("sort", false), BuilderFrame.Type.SORT)
        }
        sortHomeUI = sortUI.build()

        val sortViewQuest = releaseFile("ui/handbook/sortViewQuest.yml", false)
        sortViewQuestUI.loadFrame(sortViewQuest.getObject("for"), BuilderFrame.Type.CUSTOM)

        val innerQuestList = releaseFile("ui/handbook/innerQuestList.yml", false)
        innerQuestListUI.loadFrame(innerQuestList.getObject("for"), BuilderFrame.Type.CUSTOM)

        val qNoteYaml = releaseFile("ui/handbook/questNote.yml", false)
        questNoteUI.loadFrame(qNoteYaml.getObject("already"), BuilderFrame.Type.CUSTOM)

        val iNoteYaml = releaseFile("ui/handbook/innerQuestNote.yml", false)
        innerQuestNoteUI.loadFrame(iNoteYaml.getObject("inner"), BuilderFrame.Type.CUSTOM)
    }

    private fun getTextComp(id: String): TextComponent? {
        return sortViewQuestUI.textComponent[id]
    }

    /**
     * 为用户编译任务手册的任务信息
     */
    fun questSortBuild(player: Player, sort: String): MutableList<TellrawJson> {
        val pData = DataStorage.getPlayerData(player)
        val qData = pData.questDataList
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewQuestUI.copy()
        val textCompNo = getTextComp("noClick")?: return mutableListOf()
        val textCompClick = getTextComp("click")?: return mutableListOf()
        sortView.textComponent.clear()

        qData.values.forEach {
            val id = it.questID
            val m = QuestManager.getQuestModule(id)
            if (m?.sort == sort && it.state != StateType.FINISH && !hasDisplay.contains(id)) {
                hasDisplay.add(id)
                val textComp = textCompClick.copy()
                setText(player, id, it.questUUID.toString(), sortView, textComp)
            }
        }

        val sortList = sortQuest[sort]
        sortList?.forEach {
            val id = it.questID
            if (!hasDisplay.contains(id)) {
                val noText = textCompNo.copy()
                setText(player, id,  "", sortView, noText)
            }
        }

        return sortView.build(player)
    }

    fun innerQuestListBuild(player: Player, questUUID: String): MutableList<TellrawJson> {
        val qData = QuestManager.getQuestData(player, UUID.fromString(questUUID))?: return mutableListOf()
        val ui = innerQuestListUI.copy()
        val textComponent = innerQuestListUI.textComponent["click"]?: return mutableListOf()
        ui.textComponent.clear()
        val qID = qData.id
        setInnerText(player, qID, questUUID, qData.questInnerData.innerQuestID, ui, textComponent)
        qData.finishedList.forEach {
            setInnerText(player, qID, questUUID, it, ui, textComponent)
        }
        return ui.build(player)
    }

    private fun setInnerText(player: Player, questID: String, questUUID: String, innerID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        val innerModule = QuestManager.getInnerModule(questID, innerID)?: return
        val fork = builderFrame.noteComponent["fork"]?: return
        builderFrame.noteComponent[innerID] = NoteComponent(fork.note.copy(), fork.condition(player).copy())
        builderFrame.noteComponent.values.forEach {
            val note = it.note
            for (i in note.indices) {
                note[i] = note[i].replaceWithOrder(innerModule.name, innerID)
            }
        }
        textComponent.command = "/qen handbook inner $questUUID $innerID"
        builderFrame.textComponent[innerID] = textComponent
    }

    fun questNoteBuild(player: Player, questID: String, questUUID: String): MutableList<TellrawJson> {
        val ui = questNoteUI.copy()
        ui.noteComponent.values.forEach {
            it.note = listReply(player, questID, questUUID, it.note)
            it.note = descSet(it.note, "note", questID)
            it.condition = listReply(player, questID, questUUID, it.condition)
        }
        ui.textComponent.values.forEach {
            it.command = it.command.replace("{2}", questUUID, true)
        }
        return ui.build(player)
    }

    fun innerQuestNoteBuild(player: Player, questUUID: UUID, innerID: String): MutableList<TellrawJson> {
        val ui = innerQuestNoteUI.copy()
        val innerData = QuestManager.getInnerQuestData(player, questUUID, innerID)?: return mutableListOf()
        val questID = innerData.questID
        val innerModule = QuestManager.getInnerModule(questID, innerID)?: return mutableListOf()
        var time = "null"
        val endDate = innerData.end
        if (endDate != null) {
            time = TimeUtil.remainDate(player, innerData.state, endDate)
        }
        ui.noteComponent.values.forEach {
            val note = it.note
            for (i in 0 until note.size) {
                note[i] = note[i].replaceWithOrder(innerModule.name, innerData.state.toUnit(player), time)
            }
            it.note = descSet(it.note, "", questID, innerID)
        }
        return ui.build(player)
    }

    fun targetNodeBuild(player: Player, questUUID: UUID, innerID: String): MutableList<TellrawJson> {
        val list = mutableListOf<TellrawJson>()
        val innerData = QuestManager.getInnerQuestData(player, questUUID, innerID)?: return list
        innerData.target.values.forEach {
            allTargetNoteBuild(player, innerData, it.questTarget).forEach { t ->
                list.add(t)
            }
        }
        return list
    }

    private fun allTargetNoteBuild(player: Player, innerData: QuestData, target: QuestTarget): MutableList<TellrawJson> {
        val tData = innerData.getTargetData(target.name)?: return mutableListOf()
        val targetUI = buildFrame().loadFrame(target.ui).copy()
        targetUI.noteComponent.values.forEach {
            val note = it.note
            for (i in 0 until note.size) {
                note[i] = note[i].replaceWithOrder(tData.schedule)
            }
        }
        return targetUI.build(player)
    }

    private fun setText(player: Player, questID: String, questUUID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        if (builderFrame.textComponent.containsKey(questUUID)) return
        val fork = builderFrame.noteComponent["fork"]?: return

        builderFrame.noteComponent[questID] = NoteComponent(fork.note.copy(), fork.condition(player).copy())

        if (!builderFrame.textCondition(player, listReply(player, questID, questUUID, textComponent.condition))) return

        textComponent.hover = descSet(textComponent.hover, "info", questID)

        if (questUUID.isNotEmpty()) {
            textComponent.command = "/qen handbook info $questID $questUUID"
        }

        builderFrame.noteComponent.values.forEach {
            if (!it.fork) {
                it.note = listReply(player, questID, questUUID, it.note(player))
                it.condition = listReply(player, questID, questUUID, it.condition(player))
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

    private fun getDescMap(questID: String, innerID: String, sign: String): List<String>? {
        if (innerID.isNotEmpty()) {
            val innerModule = QuestManager.getInnerModule(questID, innerID)?: return null
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
        return qData.state == StateType.FINISH
    }

    fun listReply(player: Player, questID: String, questUUID: String, list: MutableList<String>): MutableList<String> {
        for (i in 0 until list.size) {
            val qModule = QuestManager.getQuestModule(questID)?: break
            val stateUnit = StateType.NOT_ACCEPT.toUnit(player)
            list[i] = list[i].replaceWithOrder(
                qModule.name, // {0}
                questID, // {1}
                if (questUUID.isEmpty()) questID else questUUID,
                if (questUUID.isNotEmpty()) QuestManager.getQuestData(player, UUID.fromString(questUUID))?.state?.toUnit(player) ?: stateUnit else stateUnit,
                "type " + !accept(player, questID), // {4}
                "type " + accept(player, questID),
                "type " + !finish(player, questID),
                "type " + finish(player, questID)
            )
        }
        return list
    }

}