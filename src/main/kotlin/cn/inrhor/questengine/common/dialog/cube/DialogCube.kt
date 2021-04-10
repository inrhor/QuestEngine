package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.common.dialog.animation.item.DialogItem
import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextAnimation
import cn.inrhor.questengine.common.dialog.animation.text.TagText
import cn.inrhor.questengine.utlis.location.FixedLocation

class DialogCube(val dialogID: String, val npcID: String,
                 var condition: MutableList<String>,
                 var dtAnimation: TextAnimation) {

    var replyCubeList: MutableList<ReplyCube> = mutableListOf()

    /**
     * 主体文字组中这一行包含的标签内容
     */
    fun getTheLineList(line: Int): MutableList<TagText> {
        return dtAnimation.getTextContent(line)
    }

}