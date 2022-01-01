package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager

/**
 * 对话模块
 */
data class DialogModule(
    val dialog: List<String>,
    val reply: MutableList<ReplyModule>) {

    lateinit var dialogID: String

    val npcIDs = mutableListOf<String>()
    val condition = mutableListOf<String>()
    val space = SpaceDialogModule()
    val type: String = "holo"

   fun register() {
       DialogManager.register(dialogID, this)
   }
}

/**
 * 对话空间模块
 */
data class SpaceDialogModule(val enable: Boolean, val condition: List<String>) {
    constructor(): this(false, mutableListOf())
}
