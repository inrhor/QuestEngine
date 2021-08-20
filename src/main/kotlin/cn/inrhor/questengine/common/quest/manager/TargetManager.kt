package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestTarget
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.library.configuration.FileConfiguration

object TargetManager {

    val targetMap = mutableMapOf<String, ConditionType>()

    /**
     * 规范配置
     */
    fun register(name: String, meta: String, string: String) {
        set(name, meta, ConditionType(string))
    }

    fun register(name: String, meta: String, list: MutableList<String>) {
        set(name, meta, ConditionType(list))
    }

    fun set(name: String, meta: String, conditionType: ConditionType) {
        targetMap["$name-$meta"] = conditionType
    }

    fun getTargetList(yaml: FileConfiguration): MutableMap<String, QuestTarget> {
        val questTargetList = mutableMapOf<String, QuestTarget>()
        for (i in yaml.getConfigurationSection("target").getKeys(false)) {
            val s = "target.$i."
            val name = yaml.getString(s + "name") ?: "null"
            val time = yaml.getString(s + "time") ?: "always"
            val reward = yaml.getString(s + "reward") ?: "null"
            val condition = mutableMapOf<String, String>()
            val conditionList = mutableMapOf<String, MutableList<String>>()

            targetMap.forEach { (eventNameMeta, conditionType) ->
                val eventName = eventNameMeta.split("-")[0]
                if (eventName == name) {
                    for (node in yaml.getConfigurationSection("target.$i").getKeys(true)) {
                        val u = "target.$i.$node"
                        if (conditionType.content != "") {
                            if (conditionType.content == node) {
                                condition[node] = yaml.getString(u)!!
                            }
                        } else {
                            if (conditionType.contentList.contains(node)) {
                                conditionList[node] = yaml.getStringList(u)
                            }
                        }
                    }
                }
            }
            val description = yaml.getStringList(s + "description")
            val period = yaml.getInt(s+"period")
            val async = yaml.getBoolean(s+"async")
            val conditions = yaml.getStringList(s+"conditions")
            val target = QuestTarget(name, time, reward, period, async, conditions,
                condition, conditionList, description)
            questTargetList[name] = target
        }
        return questTargetList
    }

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(name: String, questData: QuestData, targetData: TargetData): Int {
        val questModule = QuestManager.getQuestModule(questData.questID)?: return 0
        if (questModule.modeType == ModeType.COLLABORATION && questModule.modeShareData && questData.teamData != null) {
            var schedule = 0
            for (mUUID in questData.teamData!!.members) {
                val m = Bukkit.getPlayer(mUUID)?: continue
                val innerData = QuestManager.getInnerQuestData(m, questData.questUUID)?: continue
                val tgData = innerData.targetsData[name]?: continue
                schedule += tgData.schedule
            }
            return schedule
        }
        if (questModule.modeType == ModeType.PERSONAL) {
            return targetData.schedule
        }
        return 0
    }

    fun runTask(pData: PlayerData, player: Player) {
        pData.questDataList.values.forEach {
            val inner = it.questInnerData
            inner.targetsData.values.forEach { t ->
                if (t.name.lowercase().startsWith("task ")) {
                    t.runTask(player, it, inner)
                }
            }
        }
    }

}