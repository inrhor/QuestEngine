package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.DialogManager

/**
 * 对话模块
 */
data class DialogModule(
    val id: String,
    val dialog: List<String>,
    val reply: MutableList<ReplyModule>) {

    lateinit var dialogID: String

    val npcIDs = mutableSetOf<String>()
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

/*
*
 * 对话属性模块
 *
class DialogModule(val dialogID: String,
                   var npcIDs: MutableList<String>,
                   var condition: MutableList<String>,
                   var type: String,
                   var dialog: MutableList<String>,
                   var playText: MutableList<TextDialogPlay>,
                   var playItem: MutableList<ItemDialogPlay>,
                   var spaceModule: SpaceModule) {

    var replyModuleList: MutableList<ReplyModule> = mutableListOf()
}

*/
/**
 * 对话空间属性模块
 *
 *//*

class SpaceModule(
    var enable: Boolean,
    var condition: MutableList<String>) {
}*/
