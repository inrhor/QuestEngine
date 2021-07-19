package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.quest.ConditionType
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.common.quest.QuestTarget
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration

object TargetManager {

    val targetMap = mutableMapOf<String, ConditionType>()

    /**
     * 规范配置
     */
    fun register(name: String, conditionType: ConditionType) {
        targetMap[name] = conditionType
    }

    fun getTargetList(yaml: FileConfiguration): MutableMap<String, QuestTarget> {
        val questTargetList = mutableMapOf<String, QuestTarget>()
        for (i in yaml.getConfigurationSection("target")!!.getKeys(false)) {
            val s = "target.$i."
            val name = yaml.getString(s + "name") ?: "null"
            val time = yaml.getString(s + "time") ?: "always"
            val reward = yaml.getString(s + "reward") ?: "null"
            val condition = mutableMapOf<String, String>()
            val conditionList = mutableMapOf<String, MutableList<String>>()

            targetMap.forEach { (eventName, conditionType) ->
                if (eventName == name) {
                    for (node in yaml.getConfigurationSection("target.$i")!!.getKeys(true)) {
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
            val target = QuestTarget(name, time, reward, condition, conditionList)
            questTargetList[name] = target
        }
        return questTargetList
    }

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(name: String, questID: String, questData: QuestData, targetData: TargetData, type: String): Int {
        val questModule = QuestManager.getQuestModule(questID)?: return 0
        if (questModule.modeType == ModeType.COLLABORATION && questModule.modeShareData && questData.teamData != null) {
            var schedule = 0
            for (mUUID in questData.teamData!!.members) {
                val m = Bukkit.getPlayer(mUUID)?: continue
                val mainData = questData.questMainData
                schedule += if (type == "main") {
                    val tgData = mainData.targetsData[name]?: continue
                    tgData.schedule
                }else {
                    val tg = QuestManager.getDoingSubTarget(m, name)?: continue
                    val subData = QuestManager.getSubQuestData(m, questID, tg.subQuestID)?: continue
                    val tgData = subData.targetsData[name]?: continue
                    tgData.schedule
                }
            }
            return schedule
        }
        if (questModule.modeType == ModeType.PERSONAL) {
            return targetData.schedule
        }
        return 0
    }

}