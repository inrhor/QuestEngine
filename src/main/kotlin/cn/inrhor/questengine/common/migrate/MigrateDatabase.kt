package cn.inrhor.questengine.common.migrate

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.TrackData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.type.DatabaseManager
import cn.inrhor.questengine.common.nav.NavData
import taboolib.common.io.newFolder
import taboolib.common.platform.ProxyCommandSender
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.module.configuration.util.getLocation
import taboolib.platform.util.toBukkitLocation
import java.util.UUID

class MigrateDatabase {

    /**
     * 从旧数据YAML迁移到新数据
     *
     * 新数据由 config 配置所选数据存储类型决定
     */
    fun oldToNew(sender: ProxyCommandSender) {
        sender.sendMessage("§f")
        sender.sendMessage("[ 迁移 ] 开始迁移 QuestEngine 数据")
        sender.sendMessage("   本次迁移工作为：旧数据YAML 迁移到 新数据 ${DatabaseManager.type}")
        // 计算耗时
        val startTime = System.currentTimeMillis()
        val database = Database.database
        // 遍历QuestEngine.plugin.dataFolder, "data"文件夹下的所有文件
        newFolder(QuestEngine.plugin.dataFolder, "data", false).listFiles()?.forEach { file ->
            // 如果文件名以 .yml 结尾
            val fileName = file.name
            // 文件名是uuid.yml
            if (fileName.endsWith(".yml", true)) {
                val uuid = UUID.fromString(fileName.substring(0, fileName.length - 4))
                // 读取文件
                val config = Configuration.loadFromFile(file)
                // 读取文件中的 "data" 节点
                config.getConfigurationSection("quest")?.getKeys(false)?.forEach { data ->
                    val questData = config.getObject<QuestData>("quest.$data", false)
                    database.createQuest(uuid, questData)
                }
                if (config.contains("track")) {
                    val trackData = config.getObject<TrackData>("track", false)
                    database.setTrack(uuid, trackData)
                }
                if (config.contains("tags")) {
                    config.getStringList("tags.tags").forEach {
                        database.addTag(uuid, it)
                    }
                }
                if (config.contains("storage")) {
                    config.getConfigurationSection("storage")?.getKeys(false)?.forEach {
                        val value = config.getString("storage.$it")
                        if (value != null) {
                            database.setStorage(uuid, it, value)
                        }
                    }
                }
                if (config.contains("navigation")) {
                    config.getConfigurationSection("navigation")?.getKeys(false)?.forEach {
                        val loc = config.getLocation("navigation.$it.location")
                        if (loc != null) {
                            val navData = NavData(it, loc.toBukkitLocation())
                            database.createNavigation(uuid, it, navData)
                        }
                    }
                }
            }
        }
        sender.sendMessage("   迁移完成，耗时 ${System.currentTimeMillis() - startTime}ms")
        sender.sendMessage("§f")
    }

}