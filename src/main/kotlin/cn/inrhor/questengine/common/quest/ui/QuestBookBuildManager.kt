package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.database.data.questData
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.utlis.copy
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderFrame
import cn.inrhor.questengine.utlis.ui.NoteComponent
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildFrame
import org.bukkit.entity.Player
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.TellrawJson
import taboolib.module.configuration.Configuration.Companion.getObject

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
     * 分类任务模块列表
     */
    val sortQuest = mutableMapOf<String, MutableSet<QuestFrame>>()

    /**
     * 任务信息手册
     */
    val questNoteUI = buildFrame()

    fun addSortQuest(sort: String, quest: QuestFrame) {
        if (sortQuest.containsKey(sort)) {
            sortQuest[sort]!!.add(quest)
            return
        }
        sortQuest[sort] = mutableSetOf(quest)
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

        val qNoteYaml = releaseFile("ui/handbook/questNote.yml", false)
        questNoteUI.loadFrame(qNoteYaml.getObject("already"), BuilderFrame.Type.CUSTOM)
    }

    private fun getTextComp(id: String): TextComponent? {
        return sortViewQuestUI.textComponent[id]
    }

    /**
     * 为用户编译任务手册的任务信息
     */
    fun questSortBuild(player: Player, sort: String): MutableList<TellrawJson> {
        val pData = player.getPlayerData()
        val qData = pData.dataContainer.quest
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewQuestUI.copy()
        val textCompNo = getTextComp("noClick")?: return mutableListOf()
        val textCompClick = getTextComp("click")?: return mutableListOf()
        sortView.textComponent.clear()

        qData.values.forEach {
            val id = it.id
            val m = id.getQuestFrame()
            if (m.group.sort == sort && it.state != StateType.FINISH && !hasDisplay.contains(id)) {
                hasDisplay.add(id)
                val textComp = textCompClick.copy()
                setText(player, id, sortView, textComp)
            }
        }

        val sortList = sortQuest[sort]
        sortList?.forEach {
            val id = it.id
            if (!hasDisplay.contains(id)) {
                val noText = textCompNo.copy()
                setText(player, id, sortView, noText)
            }
        }

        return sortView.build(player)
    }

    fun questNoteBuild(player: Player, questID: String): MutableList<TellrawJson> {
        val ui = questNoteUI.copy()
        ui.noteComponent.values.forEach {
            it.note = listReply(player, questID, it.note)
            it.note = descSet(it.note, "note", questID)
            it.condition = listReply(player, questID, it.condition)
        }
        ui.textComponent.values.forEach {
            it.command = it.command.replace("{2}", questID, true)
        }
        return ui.build(player)
    }

    fun targetNodeBuild(player: Player, questID: String): MutableList<TellrawJson> {
        val list = mutableListOf<TellrawJson>()
        val data = player.questData(questID)
        data.target.forEach {
            allTargetNoteBuild(player, data, it).forEach { t ->
                list.add(t)
            }
        }
        return list
    }

    private fun allTargetNoteBuild(player: Player, questData: QuestData, targetData: TargetData): MutableList<TellrawJson> {
        val targetUI = buildFrame().loadFrame(targetData.getTargetFrame().ui).copy()
        targetUI.noteComponent.values.forEach {
            val note = it.note
            for (i in 0 until note.size) {
                note[i] = note[i].replaceWithOrder(targetData.schedule)
            }
        }
        return targetUI.build(player)
    }

    private fun setText(player: Player, questID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        if (builderFrame.textComponent.containsKey(questID)) return
        val fork = builderFrame.noteComponent["fork"]?: return

        builderFrame.noteComponent[questID] = NoteComponent(fork.note.copy(), fork.condition(player))

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

    fun descSet(list: MutableList<String>, sign: String, questID: String): MutableList<String> {
        val desc = mutableListOf<String>()
        for (i in 0 until list.size) {
            desc.add(list[i])
        }
        return desc
    }

    fun listReply(player: Player, questID: String, list: MutableList<String>): MutableList<String> {
        for (i in 0 until list.size) {
            list[i] = listReply(player, questID, list[i])
        }
        return list
    }

    fun listReply(player: Player, questID: String, list: String): String {
        val quest = questID.getQuestFrame()
        val stateUnit = StateType.NOT_ACCEPT.toUnit(player)
        return list.replaceWithOrder(
            quest.name, // {0}
            questID, // {1}
            if (questID.isNotEmpty()) player.questData(questID).state.toUnit(player) else stateUnit,
        )
    }

}