package cn.inrhor.questengine.api.dialog

import taboolib.library.configuration.PreserveNotNull

/**
 * 回复属性模块
 */
class ReplyModule(val replyID: String, @PreserveNotNull val tagDefault: String = "   &7", @PreserveNotNull val tagChoose: String = "   &7&l[ &b&l! &7&l] &b") {

    val condition = mutableListOf<String>()
    val content = mutableListOf<String>()
    val script = mutableListOf<String>()

    constructor(): this("reply")

}