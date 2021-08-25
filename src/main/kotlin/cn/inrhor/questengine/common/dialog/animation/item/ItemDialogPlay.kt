package cn.inrhor.questengine.common.dialog.animation.item

import org.bukkit.inventory.ItemStack

/**
 * @param item 物品内容
 * @param delay 延迟播放
 */
class ItemDialogPlay(var holoID: Int, var itemID: Int, val item: ItemStack, var type: Type, val delay: Int) {

    constructor(item: ItemStack, type: Type, delay: Int) :
            this(0, 0, item, type, delay)

    enum class Type {
        SUSPEND, FIXED
    }

}