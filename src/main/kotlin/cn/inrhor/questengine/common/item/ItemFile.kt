package cn.inrhor.questengine.common.item

import io.izzel.taboolib.util.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class ItemFile {

    var itemID: String? = null
    var item: ItemStack? = null

    fun init(config: ConfigurationSection) {
        if (!config.contains("material")) {
            return
        }
        this.itemID = config.name
        val material = Material.valueOf(config.getString("material")!!)
        val displayName = config.getString("displayName")
        val lore = config.getStringList("lore")
        val customModelData = config.getInt("customModelData")
        val itemBuilder = ItemBuilder(material)
        itemBuilder.name(displayName)
        itemBuilder.lore(lore)
        itemBuilder.customModelData(customModelData)
        this.item = itemBuilder.build()

        ItemManager().register(this.itemID!!, this)
    }

}