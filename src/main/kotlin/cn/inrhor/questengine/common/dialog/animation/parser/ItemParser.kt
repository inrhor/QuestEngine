package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.item.DialogItem
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.kether.KetherHandler
import org.bukkit.inventory.ItemStack

/**
 * 对话配置传递的物品内容列表，此类做解析并存储
 */
class ItemParser(private val itemContents: MutableList<String>) {

    /**
     * 列表包含的物品内容
     * 每一行仅一个物品
     */
    private var dialogItemList = mutableListOf<DialogItem>()

    fun init() {
        for (line in 0 until this.itemContents.size) {
            val script = this.itemContents[line]
            if (script.startsWith("itemNormal")) {
                val dialogItem = KetherHandler.eval(script) as DialogItem
                dialogItemList.add(dialogItem)
            }else {
                val dialogItem = DialogItem(ItemManager().get(script), 0)
                dialogItemList.add(dialogItem)
            }
        }
    }

    /**
     * 根据索引获取对话物品
     */
    fun getDialogItem(index: Int): DialogItem? {
        if (dialogItemList.size > index) return dialogItemList[index]
        return null
    }

    /**
     * 获取物品列表
     */
    fun getDialogItemList(): MutableList<ItemStack> {
        val itemList = mutableListOf<ItemStack>()
        dialogItemList.forEach { itemList.add(it.item) }
        return itemList
    }
}