package cn.inrhor.questengine.common.record

import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.hook.invero.UiType
import cn.inrhor.questengine.common.loader.ConfigReader.recordChat
import cn.inrhor.questengine.common.quest.enum.StateType
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.script.kether.evalString
import cn.inrhor.questengine.utlis.asToList
import cn.inrhor.questengine.utlis.component.NewSimpleComponent
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.common5.Demand
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.chat.colored

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
        val p = adaptPlayer(player)
        refresh(p)
        val item = (recordChat.getString("RECORD-GROUP-ITEM")?.colored()?: "RECORD-GROUP-ITEM").asToList()
        val d = Demand(item.first())
        // 数量
        val copy = Coerce.toInteger(d.get("copy"))

        val groupList = when (state) {
            StateType.DOING -> UiType.GROUP_DOING.list(player)
            StateType.FINISH -> UiType.GROUP_COMPLETE.list(player)
            else -> listOf()
        }

        val size = groupList.size
        var maxPage = page
        var newIndex = index
        var itemStr = ""

        if (size > 0) {

            // 一页数量copy，任务组总页
            maxPage = size / copy + 1
            item.removeAt(0)
            val str = item.joinToString("\n")
            // 从 index 到 copy 遍历任务组添加在新列表，并且copy数量不超过任务组列表的长度，并且设置新的变量index+copy或者index+任务组列表的长度
            val itemThePage = groupList.subList(index * copy, copy.coerceAtMost(size)).toMutableList()
            newIndex = index + itemThePage.size

            // 遍历itemThePage存入itemStr
            itemThePage.forEach {
                itemStr += player.evalString(str, "{{", "}}") { c ->
                    c.rootFrame().variables()["@QenGroupID"] = it.id
                }.colored()
            }
        }

        info("itemStr $itemStr")

        val componentText = NewSimpleComponent(
            player.evalString(
                recordChat.getString("RECORD-GROUP")?.colored()?: "RECORD-GROUP", "{{", "}}") {
                it.rootFrame().variables()["@RECORD-GROUP-END"] = player.evalString(
                    recordChat.getString("RECORD-GROUP-END")?.colored()?: "RECORD-GROUP-END", "{{", "}}") { a ->
                    a.rootFrame().variables()["@page_now"] = page
                    a.rootFrame().variables()["@page_max"] = maxPage
                    a.rootFrame().variables()["@index"] = newIndex
                }
                it.rootFrame().variables()["@page_now"] = page
                it.rootFrame().variables()["@page_max"] = maxPage
                it.rootFrame().variables()["@index"] = newIndex
                it.rootFrame().variables()["@RECORD-GROUP-ITEM"] = itemStr
        }).toBuild()

        componentText.sendTo(adaptPlayer(player))
    }

}