package cn.inrhor.questengine.api.dialog

import org.bukkit.inventory.ItemStack

/**
 * 对话属性模块
 *
 */
class DialogModule(val dialogID: String,
                   var npcID: String,
                   var condition: MutableList<String>,
                   var type: String,
                   var dialog: MutableList<String>,
                   var playText: MutableList<String>,
                   var playItem: MutableList<ItemStack>) {

    var replyModuleList: MutableList<ReplyModule> = mutableListOf()
}