package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager
import taboolib.library.configuration.PreserveNotNull

/**
 * 对话模块
 */
class DialogModule(
    @PreserveNotNull var dialog: List<String> = listOf(),
    @PreserveNotNull var reply: MutableList<ReplyModule> = mutableListOf(),
    @PreserveNotNull var npcIDs: List<String> = listOf(),
    @PreserveNotNull var condition: List<String> = listOf(),
    @PreserveNotNull var space: SpaceDialogModule = SpaceDialogModule()
) {

    lateinit var dialogID: String

    @PreserveNotNull val type: String = "holo"

   fun register() {
       DialogManager.register(dialogID, this)
   }
}

/**
 * 对话空间模块
 */
class SpaceDialogModule(val enable: Boolean, val condition: List<String>) {
    constructor(): this(false, listOf())
}
