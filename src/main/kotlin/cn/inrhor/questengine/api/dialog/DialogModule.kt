package cn.inrhor.questengine.api.dialog

/**
 * 对话属性模块
 *
 */
class DialogModule(val dialogID: String,
                   var npcID: String,
                   var condition: MutableList<String>,
                   var type: String,
                   var dialog: MutableList<String>) {

    var replyModuleList: MutableList<ReplyModule> = mutableListOf()
}