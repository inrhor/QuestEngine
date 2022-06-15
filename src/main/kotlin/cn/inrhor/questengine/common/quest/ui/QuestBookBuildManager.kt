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
import taboolib.module.chat.TellrawJson
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.sendBook

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

    fun QuestFrame.updateSortQuest(sort: String) {
        sortQuest.values.forEach {
            val i = it.iterator()
            while (i.hasNext()) {
                val n = i.next()
                if (n.id == id) {
                    i.remove()
                    break
                }
            }
        }
        sortQuest[sort]?.add(this)
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
    fun Player.questSortBuild(sort: String) {
        val pData = getPlayerData()
        val qData = pData.dataContainer.quest
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewQuestUI.copy()
        val textCompNo = getTextComp("noClick")?: return
        val textCompClick = getTextComp("click")?: return
        sortView.textComponent.clear()

        qData.values.forEach {
            val id = it.id
            val m = id.getQuestFrame()
            if (m.group.sort == sort && it.state != StateType.FINISH && !hasDisplay.contains(id)) {
                hasDisplay.add(id)
                val textComp = textCompClick.copy()
                setText(this@questSortBuild, id, sortView, textComp)
            }
        }

        val sortList = sortQuest[sort]
        sortList?.forEach {
            val id = it.id
            if (!hasDisplay.contains(id)) {
                val noText = textCompNo.copy()
                setText(this@questSortBuild, id, sortView, noText)
            }
        }

        sendBook {
            sortView.build(player).forEach {
                write(it)
            }
        }
    }

    fun Player.questNoteBuild(questID: String) {
        val ui = questNoteUI.copy()
        ui.textComponent.values.forEach {
            it.command = listReply(this, questID, it.command)
        }
        ui.noteComponent.values.forEach {
            it.note = listReply(this, questID, it.note)
            it.note = descSet(it.note, "note", questID)
            it.condition = listReply(this, questID, it.condition)
        }
        sendBook {
            ui.build(player).forEach { write(it) }
        }
    }

    fun Player.targetNodeBuild(questID: String) {
        val list = mutableListOf<TellrawJson>()
        val data = questData(questID)
        data.target.forEach {
            allTargetNoteBuild(this, data, it).forEach { t ->
                list.add(t)
            }
        }
        sendBook {
            list.forEach { write(it) }
        }
    }

    private fun allTargetNoteBuild(player: Player, questData: QuestData, targetData: TargetData): MutableList<TellrawJson> {
        val targetUI = buildFrame().loadFrame(targetData.getTargetFrame().ui).copy()
        targetUI.noteComponent.values.forEach {
            val note = it.note
            for (i in 0 until note.size) {
                note[i] = note[i]
                    .replace("{{targetID}}", targetData.id)
                    .replace("{{questID}}", questData.id)
            }
            it.note = it.note.replacePlaceholder(player).toMutableList()
        }
        return targetUI.build(player)
    }

    private fun setText(player: Player, questID: String, builderFrame: BuilderFrame, textComponent: TextComponent) {
        if (builderFrame.textComponent.containsKey(questID)) return
        val fork = builderFrame.noteComponent["fork"]?: return

        builderFrame.noteComponent[questID] = NoteComponent(fork.note.copy(), listReply(player, questID, fork.condition))

        if (!builderFrame.textCondition(player, listReply(player, questID, textComponent.condition))) return

        textComponent.hover = descSet(textComponent.hover, "info", questID)
        textComponent.command = textComponent.command.replace("{{questID}}", questID)

        builderFrame.noteComponent.values.forEach {
            if (!it.fork) {
                it.note = listReply(player, questID, it.note)
                it.condition = listReply(player, questID, it.condition)
            }
        }

        builderFrame.textComponent[questID] = textComponent
    }

    fun descSet(list: MutableList<String>, sign: String, questID: String): MutableList<String> {
        for (i in 0 until list.size) {
            if (sign.isNotEmpty()) {
                if (list[i] == "#quest-desc-$sign") {
                    list[i] = questID.getQuestFrame().note
                }else if (list[i] == "#group-desc-$sign") {
                    list[i] = questID.getQuestFrame().group.note
                }
            }
        }
        return list
    }

    fun listReply(player: Player, questID: String, list: MutableList<String>): MutableList<String> {
        for (i in 0 until list.size) {
            list[i] = listReply(player, questID, list[i])
        }
        return list
    }

    fun listReply(player: Player, questID: String, list: String): String {
        return list.replace("{{questID}}", questID).replacePlaceholder(player)
    }

}