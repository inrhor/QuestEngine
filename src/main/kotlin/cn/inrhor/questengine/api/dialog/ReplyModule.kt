package cn.inrhor.questengine.api.dialog

/**
 * 回复属性模块
 */
data class ReplyModule(val replyID: String) {

    val condition = mutableListOf<String>()
    val content = mutableListOf<String>()
    val script = mutableListOf<String>()

}

/*class ReplyModule(val dialogID: String,
                  val replyID: String,
                  var content: MutableList<String>,
                  var script: MutableList<String>,
                  var condition: MutableList<String>,
                  var textList: MutableList<TextDisplay> = mutableListOf(),
                  var itemList: MutableList<ItemDisplay> = mutableListOf()) {

    init {
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

}*/
