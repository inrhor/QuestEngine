package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.common.dialog.cube.TextAnimation

class DialogFile(
    val dialogID: String,
    val target: String,
    val condition: MutableList<String>,

    val ownLocation: String,

    val ownTextAddLocation: String,
    val ownTextContent: MutableList<String>,

    val ownItemAddLocation: String,
    val ownItemContent: MutableList<String>,

    val frame: Int
) {

    /**
     * 处理动画到表中
     */
    fun animation() {
        val textAnimation =
            TextAnimation(dialogID, ownTextContent)
        textAnimation.init()
    }

}