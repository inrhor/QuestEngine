package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager


/**
 * 对话模块
 */
class DialogModule(
    var dialogID: String = "null",
    var type: DialogType = DialogType.CHAT,
    var template: String = "",
    var dialog: List<String> = listOf(),
    var reply: MutableList<ReplyModule> = mutableListOf(),
    var npcIDs: List<String> = listOf(),
    var condition: String = "",
    var space: SpaceDialogModule = SpaceDialogModule(),
    var speed: Int = 1, var flag: List<String> = listOf(),
    var replyChoose: String = "",
    var replyDefault: String = ""
) {

   fun register() {
       DialogManager.register(dialogID, this)
   }
}

enum class DialogType {
    ALL, CHAT, HOLO
}

/**
 * 对话空间模块
 */
class SpaceDialogModule(val enable: Boolean, val condition: String) {
    constructor(): this(false, "")
}
