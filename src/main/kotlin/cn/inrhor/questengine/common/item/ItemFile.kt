package cn.inrhor.questengine.common.item

import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack

class ItemFile(val itemID: String) {

    lateinit var itemStack: ItemStack

    fun init(config: ConfigurationSection) {
        itemStack = config.getItemStack("")?: return

        ItemManager.register(itemID, this)
    }

}