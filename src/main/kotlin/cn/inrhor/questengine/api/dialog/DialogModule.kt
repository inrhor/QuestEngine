package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager


/**
 * 对话模块
 */
class DialogModule(
     var dialog: List<String> = listOf(),
     var reply: MutableList<ReplyModule> = mutableListOf(),
     var npcIDs: List<String> = listOf(),
     var condition: String = "",
     var space: SpaceDialogModule = SpaceDialogModule()
) {

    lateinit var dialogID: String

     val type: String = "holo"

   fun register() {
       DialogManager.register(dialogID, this)
   }
}

/**
 * 对话空间模块
 */
class SpaceDialogModule(val enable: Boolean, val condition: String) {
    constructor(): this(false, "")
}
