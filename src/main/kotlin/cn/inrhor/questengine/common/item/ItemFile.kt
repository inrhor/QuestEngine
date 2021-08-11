package cn.inrhor.questengine.common.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.setItemStack

class ItemFile(val itemID: String) {

    var item = ItemStack(Material.STONE)

    fun init(config: ConfigurationSection) {
        config.setItemStack(itemID, item)

        ItemManager.register(itemID, this)
    }

}