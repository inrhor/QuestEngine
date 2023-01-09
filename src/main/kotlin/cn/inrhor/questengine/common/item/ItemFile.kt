package cn.inrhor.questengine.common.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Configuration

class ItemFile(
    val itemID: String,
    val config: Configuration,
    var itemStack: ItemStack = ItemStack(Material.STONE)) {

    init {
        itemStack = config.getItemStack(this.itemID)?: ItemStack(Material.STONE)
        ItemManager.register(itemID, this)
    }

}