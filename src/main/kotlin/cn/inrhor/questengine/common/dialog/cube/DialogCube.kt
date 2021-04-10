package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextAnimation
import cn.inrhor.questengine.common.dialog.animation.text.TagText

class DialogCube(val dialogID: String,
                 val npcID: String,
                 var condition: MutableList<String>,
                 var dialog: MutableList<String>,
                 var textAnimation: TextAnimation,
                 var itemParser: ItemParser) {

    var replyCubeList: MutableList<ReplyCube> = mutableListOf()

    /**
     * 主体文字组中这一行包含的标签内容
     */
    fun getTheLineList(line: Int): MutableList<TagText> {
        return textAnimation.getTextContent(line)
    }

}