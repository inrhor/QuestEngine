package cn.inrhor.questengine.common.dialog.animation.parser

import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.hologram.HoloIDManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.common.kether.KetherHandler
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * 注册对话传递物品列表，此类做解析并存储
 *
 * 允许 dialog & reply 使用
 */
class ItemParser(private val itemContents: MutableList<String>) {

    /**
     * 列表包含的物品内容
     * 每一行仅一个物品
     */
    val dialogItemList = mutableListOf<ItemDialogPlay>()

    fun init(holoID: String) {
        for (line in 0 until this.itemContents.size) {
            val script = this.itemContents[line]
            if (script.uppercase(Locale.getDefault()).startsWith("ITEMNORMAL")) {
//                val id = HoloIDManager().generate(holoID, line, "text")
                val dialogItem = KetherHandler.eval(script) as ItemDialogPlay
                dialogItemList.add(dialogItem)
            }else {
                val dialogItem = ItemDialogPlay(ItemManager().get(script), 0)
                dialogItemList.add(dialogItem)
            }
        }
    }

    /**
     * 根据索引获取对话物品
     */
    fun getDialogItem(index: Int): ItemDialogPlay? {
        if (dialogItemList.size > index) return dialogItemList[index]
        return null
    }

    /**
     * 获取物品列表
     */
    /*fun getDialogItemList(): MutableList<ItemStack> {
        val itemList = mutableListOf<ItemStack>()
        dialogItemList.forEach { itemList.add(it.item) }
        return itemList
    }*/
}