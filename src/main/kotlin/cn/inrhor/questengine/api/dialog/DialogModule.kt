package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.script.kether.runEvalSet
import org.bukkit.entity.Player


/**
 * 对话模块
 */
class DialogModule(
    var dialogID: String = "null",
    var hook: String = "",
    var type: DialogType = DialogType.CHAT,
    var template: String = "",
    var dialog: List<String> = listOf(),
    var reply: MutableList<ReplyModule> = mutableListOf(),
    var npcIDs: List<String> = listOf(),
    var condition: String = "",
    var space: SpaceDialogModule = SpaceDialogModule(),
    var speed: Int = 1, var flag: List<String> = listOf(),
    var replyChoose: String = "",
    var replyDefault: String = "",
    val cases: MutableList<LogicModule> = mutableListOf()
)

enum class DialogType {
    ALL, CHAT, HOLO
}

/**
 * 对话空间模块
 */
class SpaceDialogModule(var enable: Boolean = false, var condition: String = "")

/**
 * 逻辑模式
 */
class LogicModule(val condition: String = "", val send: String = "", val action: String = "") {

    fun run(player: Player): Boolean {
        return run(mutableSetOf(player))
    }

    fun run(players: Set<Player>): Boolean {
        if (condition.isNotEmpty() && !runEvalSet(players, condition)) return false
        if (send.isNotEmpty()) {
            DialogManager.sendDialog(players.first(), send)
        }
        runEvalSet(players, action)
        return true
    }

}