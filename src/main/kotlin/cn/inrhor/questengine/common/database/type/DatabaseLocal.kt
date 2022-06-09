package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.TagsData
import cn.inrhor.questengine.common.database.data.quest.*
import cn.inrhor.questengine.common.database.data.tagsData
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.module.configuration.Configuration.Companion.setObject
import java.io.File
import java.util.*

class DatabaseLocal: Database() {

    private fun UUID.playerFile(): File {
        val data = newFile(QuestEngine.plugin.dataFolder, "data", folder = true)
        return newFile(data, "$this.yml")
    }

    fun UUID.getLocal(): Configuration {
        return Configuration.loadFromFile(playerFile())
    }

    override fun removeQuest(player: Player, questID: String) {
        val uuid = player.uniqueId
        val data = uuid.getLocal()
        data["quest.$questID"] = null
        data.saveToFile(uuid.playerFile())
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = uuid.getLocal()
        val questDataMap = mutableMapOf<String, QuestData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")!!.getKeys(false).forEach {
                val questData = data.getObject<QuestData>(it, false)
                questData.target.forEach { e-> e.load(player) }
                questDataMap[it] = questData
            }
        }
        val pData = player.getPlayerData()
        pData.dataContainer.quest = questDataMap
        pData.dataContainer.tags = TagsData(data.getStringList("tags").toMutableSet())
    }

    override fun push(player: Player) {
        val uuid = player.uniqueId
        val pData = player.getPlayerData()
        val data = uuid.getLocal()
        pData.dataContainer.quest.forEach { (t, u) ->
            data.setObject("quest.$t", u)
        }
        data.setObject("tags", player.tagsData())
        data.saveToFile(uuid.playerFile())
    }
}