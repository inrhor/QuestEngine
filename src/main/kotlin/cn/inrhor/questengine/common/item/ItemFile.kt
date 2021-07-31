package cn.inrhor.questengine.common.item

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

class ItemFile {

    var itemID: String? = null
    var item: ItemStack? = null

    fun init(config: ConfigurationSection) {
        this.itemID = config.name
        val type = config.getString("material")?: "stone"
        val material = XMaterial.valueOf(type)
        val displayName = config.getString("displayName")
        val lore = config.getStringList("lore")
        val customModelData = config.getInt("customModelData")
        val itemBuilder = buildItem(material) {
            name = displayName
            this.lore.addAll(lore)
            this.customModelData = customModelData
            colored()
            build()
        }
        this.item = itemBuilder

        ItemManager.register(this.itemID!!, this)
    }

}