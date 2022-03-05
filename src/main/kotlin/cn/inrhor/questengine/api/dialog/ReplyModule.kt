package cn.inrhor.questengine.api.dialog

/**
 * 回复属性模块
 */
class ReplyModule(val replyID: String, val tagDefault: String = "", val tagChoose: String = "") {

    val condition = mutableListOf<String>()
    val content = mutableListOf<String>()
    val script = mutableListOf<String>()

    constructor(): this("reply")

}