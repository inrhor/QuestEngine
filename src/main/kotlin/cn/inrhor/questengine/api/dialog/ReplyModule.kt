package cn.inrhor.questengine.api.dialog

/**
 * 回复属性模块
 */
class ReplyModule(val replyID: String,
                  var content: MutableList<String>,
                  var script: MutableList<String>) {
}