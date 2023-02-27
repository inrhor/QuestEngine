package cn.inrhor.questengine.common.item

import cn.inrhor.questengine.utlis.file.FileUtil
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.common.util.setSafely
import taboolib.module.configuration.Configuration
import taboolib.module.kether.ScriptContext
import taboolib.module.lang.sendLang
import taboolib.module.nms.MinecraftVersion
import java.io.File

object ItemManager {
    /**
     * 成功注册的物品
     */
    private var itemFileMap = mutableMapOf<String, ItemFile>()

    /**
     * 注册物品
     */
    fun register(itemID: String, itemFile: ItemFile) {
        if (exist(itemID)) {
            console().sendLang("ITEM.EXIST_ITEM_ID", UtilString.pluginTag, itemID)
            return
        }
        itemFileMap[itemID] = itemFile
    }

    /**
     * 加载并注册物品文件
     */
    fun loadItem() {
        val itemFolder = FileUtil.getFile("space/item/", "ITEM-NO_FILES", true, "example")
        FileUtil.getFileList(itemFolder).forEach{
            checkRegItem(it)
        }
    }

    /**
     * 检查和注册物品
     */
    private fun checkRegItem(file: File) {
        val yaml = Configuration.loadFromFile(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("ITEM.EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        yaml.getKeys(false).forEach {
            ItemFile(it, yaml)
        }
    }

    /**
     * 物品ID 是否存在
     */
    fun exist(itemID: String) = itemFileMap.contains(itemID)

    /**
     * 获取物品
     */
    fun get(itemID: String) = (itemFileMap[itemID]?: error("unknown item")).itemStack

    fun clearMap() = itemFileMap.clear()

    fun itemHook(player: Player,
                 itemID: String,
                 data: List<String>,
                 note: List<String>,
                 key: String, variable: (ScriptContext) -> Unit): ItemStack {
        val item = get(itemID)
        val meta = item.itemMeta
        val cmd = if (MinecraftVersion.major >= 6) if (meta?.hasCustomModelData() == true) meta.customModelData else 0 else 0
        val i = ItemElement(item.type.name,
            meta?.displayName?:"", meta?.lore?: listOf(), cmd, data
        )
        data.forEach {
            val s = it.uppercase()
            if (s.contains("ICON:")) {
                i.material = s.split(":")[1]
            }else if (s.contains("MODEL-DATA:")) {
                i.modelData = s.split(":")[1].toInt()
            }
        }
        if (note.isNotEmpty() && key.isNotEmpty()) {
            val lore = i.lore.toMutableList()
            if (lore.isNotEmpty()) {
                for (index in 0 until lore.size) {
                    val s = lore[index]
                    if (s == key) {
                        for (ni in note.indices) {
                            lore.setSafely(index+ni, note[ni], "")
                        }
                        break
                    }
                }
                i.lore = lore
            }
        }
        return i.itemStack(player) { variable(it) }
    }
}