package cn.inrhor.questengine.common.quest.ui

import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.QuestState
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.utlis.file.releaseFile
import cn.inrhor.questengine.utlis.ui.BuilderFrame
import cn.inrhor.questengine.utlis.ui.NoteComponent
import cn.inrhor.questengine.utlis.ui.TextComponent
import cn.inrhor.questengine.utlis.ui.buildFrame
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.module.chat.TellrawJson

/**
 * 任务手册分类
 */
object QuestSortManager {

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
        info("build")
        val pData = DataStorage.getPlayerData(player)
        val qData = pData.questDataList
        val hasDisplay = mutableSetOf<String>()
        val sortView = sortViewUI.copy()
        info("next")
        val textCompNo = getTextComp("for.noClick")?: return mutableListOf()
        info("haha")
        val textCompClick = getTextComp("for.click")?: return mutableListOf()
        info("sbsb")
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
            info("qen $id  "+hasDisplay.contains(id))
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
        val noteList = mutableListOf<String>()
        fork.note.forEach {
            noteList.add(it)
        }
        builderFrame.noteComponent[questID] = NoteComponent(noteList)

        val c = textComponent.condition
        for (i in 0 until c.size) {
            val it = c[i]
            if (it == "#!quest-accept") {
                c[i] = "type "+!accept(player, questID)
            }else if (it == "#quest-accept") {
                c[i] = "type "+accept(player, questID)
            }
        }
        if (!builderFrame.textCondition(player, c)) return

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
        builderFrame.noteComponent.forEach { t, it ->
            if (!it.fork) {
                info("replace $t  it ${it.note}")
                val d = it.note
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
            }
        }

        builderFrame.noteComponent["for.fork"]!!.note.forEach {
            info("for.fork $it  "+"fork is "+builderFrame.noteComponent["for.fork"]!!.fork)
        }

        builderFrame.textComponent[questID] = textComponent
    }

    private fun accept(player: Player, questID: String): Boolean {
        return QuestManager.existQuestData(player.uniqueId, questID)
    }

}