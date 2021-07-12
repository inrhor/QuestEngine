package cn.inrhor.questengine.common.item

import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.HashMap
import java.util.LinkedHashMap

object ItemManager {
    /**
     * 成功注册的物品
     */
    private var itemFileMap: HashMap<String, ItemFile> = LinkedHashMap()

    /**
     * 注册物品
     */
    fun register(itemID: String, itemFile: ItemFile) {
        if (exist(itemID)) {
            TLocale.sendToConsole("ITEM.EXIST_ITEM_ID", UseString.pluginTag, itemID)
            return
        }
        itemFileMap[itemID] = itemFile
    }

    /**
     * 加载并注册物品文件
     */
    fun loadItem() {
        val itemFolder = GetFile().getFile("space/item", "ITEM.NO_FILES")
        GetFile().getFileList(itemFolder).forEach{
            checkRegItem(it)
        }
    }

    /**
     * 检查和注册物品
     */
    private fun checkRegItem(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            TLocale.sendToConsole("ITEM.EMPTY_CONTENT", UseString.pluginTag, file.name)
            return
        }
        for (itemID in yaml.getKeys(false)) {
            ItemFile().init(yaml.getConfigurationSection(itemID)!!)
        }
    }

    /**
     * 物品ID 是否存在
     */
    fun exist(itemID: String) = itemFileMap.contains(itemID)

    /**
     * 获取物品
     */
    fun get(itemID: String) = itemFileMap[itemID]!!.item!!

    fun clearMap() = itemFileMap.clear()
}