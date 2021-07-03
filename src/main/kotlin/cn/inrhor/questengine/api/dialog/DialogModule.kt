package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.dialog.animation.text.TextDialogPlay

/**
 * 对话属性模块
 *
 */
class DialogModule(val dialogID: String,
                   var npcIDs: MutableList<String>,
                   var condition: MutableList<String>,
                   var type: String,
                   var dialog: MutableList<String>,
                   var playText: MutableList<TextDialogPlay>,
                   var playItem: MutableList<ItemDialogPlay>) {

    var replyModuleList: MutableList<ReplyModule> = mutableListOf()
}