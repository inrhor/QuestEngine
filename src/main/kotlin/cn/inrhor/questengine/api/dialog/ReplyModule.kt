package cn.inrhor.questengine.api.dialog

import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.dialog.optional.holo.reply.ItemDisplay
import cn.inrhor.questengine.common.dialog.optional.holo.reply.TextDisplay
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.subAfter
import java.util.*

/**
 * 回复属性模块
 */
class ReplyModule(val dialogID: String,
                  val replyID: String,
                  var content: MutableList<String>,
                  var script: MutableList<String>,
                  var textList: MutableList<TextDisplay>,
                  var itemList: MutableList<ItemDisplay>) {

    constructor(dialogID: String, replyID: String, content: MutableList<String>, script: MutableList<String>) :
            this(dialogID, replyID, content, script, mutableListOf(), mutableListOf())

    fun holoInit() {
        var textLine = 0
        var itemLine = 0
        for (i in content) {
            val iUc = i.uppercase()
            when {
                iUc.startsWith("TEXT") -> {
                    val holoID = HoloIDManager.generate(dialogID, replyID, textLine, "text")
                    textLine++
                    val get = i.substring(i.indexOf(" ")+1)
                    val textDisplay = TextDisplay(holoID, get)
                    textList.add(textDisplay)
                }
                iUc.startsWith("ITEM") -> {
                    val holoID = HoloIDManager.generate(dialogID, replyID, itemLine, "item")
                    val itemID = HoloIDManager.generate(dialogID, replyID, itemLine, "itemStack")
                    itemLine++
                    val get = i.split(" ")
                    val type = if (get[2].lowercase() == "suspend") ItemDialogPlay.Type.SUSPEND else ItemDialogPlay.Type.FIXED
                    val item = ItemManager.get(get[1])
                    val itemDisplay = ItemDisplay(holoID, itemID, item, type)
                    itemList.add(itemDisplay)
                }
            }
        }
    }

}