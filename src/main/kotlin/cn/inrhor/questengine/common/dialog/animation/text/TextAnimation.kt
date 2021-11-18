package cn.inrhor.questengine.common.dialog.animation.text

/**
 * 处理一行多个独立标签集合动态文字
 */
class TextAnimation(
    val dialogID: String,
    val line: Int,
    val script: String,
    val dialogTextList: MutableList<TextDialogPlay>) {

    init {
        val texts = listOf<String>()

    }


}