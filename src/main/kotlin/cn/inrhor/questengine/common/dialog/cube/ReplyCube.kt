package cn.inrhor.questengine.common.dialog.cube

import cn.inrhor.questengine.common.dialog.animation.item.DialogItem
import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.location.FixedLocation

class ReplyCube(val replyID:String,
                var textAddLoc: FixedLocation, var textContent: MutableList<String>,
                var itemAddLoc: FixedLocation, var itemContent: ItemParser) {

    /**
     * 主体物品组中这一行的对话物品
     */
    fun getTheLineItem(line: Int): DialogItem {
        return itemContent.getDialogItem(line)!!
    }
}