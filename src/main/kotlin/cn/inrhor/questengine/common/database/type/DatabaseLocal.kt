package cn.inrhor.questengine.common.database.type

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.common.database.Database
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.QuestMainData
import cn.inrhor.questengine.common.database.data.quest.QuestOpenData
import cn.inrhor.questengine.common.quest.QuestStateUtil
import cn.inrhor.questengine.common.quest.QuestTarget
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class DatabaseLocal: Database() {

    fun getLocal(uuid: UUID): YamlConfiguration? {
        val file = File(QuestEngine.plugin.dataFolder, "data/$uuid")
        if (!file.exists()) return null
        return YamlConfiguration.loadConfiguration(file)
    }

    override fun pull(player: Player) {
        val uuid = player.uniqueId
        val data = getLocal(uuid)?: return
        val questDataMap = mutableMapOf<String, QuestData>()
        if (data.contains("quest")) {
            data.getConfigurationSection("quest")!!.getKeys(false).forEach {
                val node = "quest.$it."
                val questID = it
                val schedule = data.getInt(node+"schedule")
                var state = QuestStateUtil.strToState(data.getString(node+"state")?: "IDLE")

                val nodeMain = node+"mainQuest."
                val mainQuestID = data.getString(nodeMain+"mainQuestID")?: return@forEach
                var mainState = QuestStateUtil.strToState(data.getString(nodeMain+"state")?: "IDLE")
                val nt = nodeMain+"targets."
                val mainTimeMap = mutableMapOf<String, Int>()
                val mainScheduleMap = mutableMapOf<String, Int>()
                for (name in data.getConfigurationSection(node+"targets")!!.getKeys(false)) {
                    mainTimeMap[name] = data.getInt(nt+"time")
                    mainScheduleMap[name] = data.getInt(nt+"schedule")
                }
                val mainModule = QuestManager.getMainQuestModule(questID, mainQuestID)?: return
                val targets = mainModule.questTargetList
                val mainData = QuestMainData(questID, mainQuestID, )

            }
        }
    }

    /*private fun getTargets(data: YamlConfiguration, node: String): MutableMap<String, QuestTarget> {
        val targets = mutableMapOf<String, QuestTarget>()
        data.getConfigurationSection(node+"targets")!!.getKeys(false).forEach {
            val nt = node+"targets."
            val name = data.getString(nt+"name")?: return@forEach
            val time = data.getInt(nt+"time")
        }
        return targets
    }*/

    /*private fun mainPull(data: YamlConfiguration, node: String): MutableMap<String, QuestOpenData> {
        if (data.contains("quest")) {
            data.getConfigurationSection(node)!!.getKeys(false).forEach {
                val mainID = it
                val scheduleMain = data.getInt(node+"schedule")
                var stateMain = QuestStateUtil.strToState(data.getString(node+"state")?: "IDLE")
            }
        }
    }*/

    override fun push(player: Player) {

    }

}