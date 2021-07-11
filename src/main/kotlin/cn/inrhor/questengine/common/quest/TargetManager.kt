package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.database.data.quest.QuestOpenData
import cn.inrhor.questengine.common.database.data.quest.remove.QuestMainDataL
import cn.inrhor.questengine.common.database.data.quest.remove.QuestSubDataL
import cn.inrhor.questengine.common.script.kether.KetherHandler
import org.bukkit.configuration.file.FileConfiguration

object TargetManager {

    private val targetMap = mutableMapOf<String, TargetExtend<*>>()

    fun register(name: String, targetExtend: TargetExtend<*>) {
        targetMap[name] = targetExtend
    }

    fun getTargetList(yaml: FileConfiguration): MutableMap<String, QuestTarget> {
        val questTargetList = mutableMapOf<String, QuestTarget>()
        for (i in yaml.getConfigurationSection("target")!!.getKeys(false)) {
            val s = "target.$i."
            val name = yaml.getString(s+"name")?: "null"
            val time = yaml.getString(s+"time")?: "always"
            val reward = yaml.getString(s+"reward")?: "null"
            val condition = mutableMapOf<String, String>()
            val conditionList = mutableMapOf<String, MutableList<String>>()

            targetMap.forEach { (n, t) ->
                if (n == name) {
                    for (node in yaml.getConfigurationSection("target.$i")!!.getKeys(false)) {
                        if (t.conditionMap.containsKey(node)) {
                            val conditionType = t.conditionMap[node]!!
                            val u = "target.$i.$node"
                            if (conditionType.content != "") {
                                condition[node] = yaml.getString(u)!!
                            }else {
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

    fun finishReward(questOpenData: QuestOpenData, questReward: QuestReward, content: String, repeat: Boolean) {
        val s = content.split(" ")
        val rewardID = s[0]
        val repeatModule = s[1].toBoolean()
        finishReward(questReward, rewardID, repeatModule, repeat)
        questOpenData.rewardState[rewardID] = true
    }

    private fun finishReward(questReward: QuestReward, rewardID: String, repeatModule: Boolean, repeat: Boolean) {
        if (!repeatModule && repeat) return
        val reward = questReward.finishReward[rewardID]?: return
        reward.forEach {
            KetherHandler.eval(it)
        }
    }

}