package cn.inrhor.questengine.api.dialog



/**
 * 回复属性模块
 */
class ReplyModule(val replyID: String,  val tagDefault: String = "   &7",  val tagChoose: String = "   &7&l[ &b&l! &7&l] &b") {

    val condition = ""
    val content = mutableListOf<String>()
    val script = ""

    constructor(): this("reply")

}