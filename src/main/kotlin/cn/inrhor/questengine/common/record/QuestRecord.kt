package cn.inrhor.questengine.common.record

import cn.inrhor.questengine.api.manager.DataManager.questData
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.hook.invero.UiType
import cn.inrhor.questengine.common.loader.ConfigReader.recordChat
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager.getGroupFrame
import cn.inrhor.questengine.script.kether.evalString
import cn.inrhor.questengine.utlis.asToList
import cn.inrhor.questengine.utlis.component.NewSimpleComponent
import org.bukkit.entity.Player
import taboolib.common.io.groupId
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.common5.Demand
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.colored
import taboolib.module.kether.ScriptContext

/**
 * 任务日志页面
 */
object QuestRecord {

    private fun refresh(player: ProxyPlayer) {
        ComponentText.empty().refresh().sendTo(player)
    }

    fun sendHome(player: Player) {
        val p = adaptPlayer(player)
        refresh(p)
        val componentText = Components.parseSimple(recordChat.getString("RECORD-HOME")?.colored()?: "RECORD-HOME").build()
        componentText.sendTo(adaptPlayer(player))
    }

    fun sendGroup(player: Player, index: Int = 0, page: Int = 1, state: StateType = StateType.DOING) {
        val groupList = when (state) {
            StateType.DOING -> UiType.GROUP_DOING.list(player)
            StateType.FINISH -> UiType.GROUP_COMPLETE.list(player)
            else -> listOf()
        }
        sendAction(player, index, page, state, groupList, "GROUP", "@QenGroupID")
    }

    fun sendQuest(player: Player, index: Int = 0, page: Int = 1, state: StateType = StateType.DOING, groupId: String) {
        val groupFrame = groupId.getGroupFrame()
        val questList = if (groupFrame != null) {
            when (state) {
                StateType.DOING -> UiType.QUEST_DOING.list(player, groupFrame)
                StateType.FINISH -> UiType.QUEST_COMPLETE.list(player, groupFrame)
                else -> listOf()
            }
        }else listOf()
        sendAction(player, index, page, state, questList, "QUEST", "@QenQuestID") {
            it.rootFrame().variables()["@QenGroupID"] = groupId
        }
    }

    fun sendTarget(player: Player, index: Int = 0, page: Int = 1, state: StateType = StateType.DOING, questId: String, groupId: String) {
        val questData = player.questData(questId)
        val targetList = if (questData != null) {
            when (state) {
                StateType.DOING -> UiType.TARGET_DOING.list(player, questData)
                StateType.FINISH -> UiType.TARGET_COMPLETE.list(player, questData)
                else -> listOf()
            }
        }else listOf()
        sendAction(player, index, page, state, targetList, "TARGET", "@QenTargetID") {
            it.rootFrame().variables()["@QenQuestID"] = questId
            it.rootFrame().variables()["@QenGroupID"] = groupId
        }
    }

    interface ActionFunc {
        val id: String
    }

    fun <T: ActionFunc> sendAction(
        player: Player, index: Int = 0, page: Int = 1, state: StateType = StateType.DOING,
        list: List<T> = listOf(), node: String = "GROUP", idVar: String = "@QenGroupID", variable: (ScriptContext) -> Unit = {}) {
        val p = adaptPlayer(player)
        refresh(p)

        val size = list.size
        var maxPage = page
        var newIndex = index
        var itemStr = ""
        var lastIndex = index

        val meta = "$node-$state"

        if (size > 0) {
            val item = (recordChat.getString("RECORD-$meta-ITEM")?.colored()?: "RECORD-$meta-ITEM").asToList()
            val d = Demand(item.first())
            // 数量
            val copy = Coerce.toInteger(d.get("copy"))

            if (copy > 0) {
                val toIndex = (index * copy + copy).coerceAtMost(size)
                if (index <= toIndex) {
                    val itemThePage = list.subList(index, toIndex)
                    maxPage = (size / copy) + if (size % copy > 0) 1 else 0
                    item.removeAt(0)
                    val str = item.joinToString("\n")
                    itemThePage.forEach {
                        itemStr += player.evalString(str, "{{", "}}") { c ->
                            c.rootFrame().variables()[idVar] = it.id
                            c.rootFrame().variables()["@state"] = state.name
                            variable(c)
                        }.colored()
                    }
                    val itemPageSize = itemThePage.size
                    newIndex = if (maxPage > page) index + itemPageSize else index
                    lastIndex = if (page > 0) index - copy else index
                }
            }
        }

        // 下一页：doing page index
        val nextCmd = "${state.name} ${if (page < maxPage) page+1 else page} $newIndex"
        // 上一页
        val lastCmd = "${state.name} ${if (page > 1) page-1 else 1} ${if (lastIndex >= 0) lastIndex else 0}"

        fun addVar(it: ScriptContext) {
            it.rootFrame().variables()["@page_now"] = page
            it.rootFrame().variables()["@page_max"] = maxPage
            it.rootFrame().variables()["@next_cmd"] = nextCmd
            it.rootFrame().variables()["@last_cmd"] = lastCmd
            it.rootFrame().variables()["@state"] = state.name
            variable(it)
        }

        val componentText = NewSimpleComponent(
            player.evalString(
                recordChat.getString("RECORD-$meta")?.colored()?: "RECORD-$meta", "{{", "}}") {
                it.rootFrame().variables()["@RECORD-$meta-END"] = player.evalString(
                    recordChat.getString("RECORD-$meta-END")?.colored()?: "RECORD-$meta-END", "{{", "}}") { a ->
                    addVar(a)
                }
                addVar(it)
                it.rootFrame().variables()["@RECORD-$meta-ITEM"] = itemStr
            }).toBuild()

        componentText.sendTo(adaptPlayer(player))
    }

}