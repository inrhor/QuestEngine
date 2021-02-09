package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.item.DialogItem
import cn.inrhor.questengine.common.dialog.animation.text.TagText
import cn.inrhor.questengine.common.item.ItemManager
import java.util.regex.Pattern

/**
 * 对话配置传递的物品内容列表，此类做解析并存储
 */
class ItemParser(private val itemContents: MutableList<String>) {

    /**
     * 列表包含的物品内容
     * 每一行仅一个物品
     */
    private var itemList = mutableListOf<DialogItem>()

    fun init() {
        for (line in 0 until this.itemContents.size) {
            val pAttribute = Pattern.compile("\\[(.*?)]")
            val attribute = pAttribute.matcher(this.itemContents[line])
            val attributes = mutableListOf<String>()
            while (attribute.find()) {
                attributes.add(attribute.group(1))
            }
            val itemID = attributes[1]
            if (!ItemManager().exist(itemID)) {
                // say
                continue
            }
            val delay = attributes[0].toInt()
            val item = ItemManager().get(itemID)!!.item!!
            val dialogItem = DialogItem(item, delay)
            itemList.add(dialogItem)
        }
    }

    /**
     * 根据行数获取对话物品
     */
    fun getDialogItem(line: Int): DialogItem? {
        if (itemList.size > line) return itemList[line]
        return null
    }
}