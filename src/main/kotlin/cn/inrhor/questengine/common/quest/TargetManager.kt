package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.TargetExtend
import org.bukkit.configuration.file.FileConfiguration

object TargetManager {

    val targetMap = mutableMapOf<String, TargetExtend<*>>()

    fun register(name: String, targetExtend: TargetExtend<*>) {
        targetMap[name] = targetExtend
    }

    fun getTargetList(yaml: FileConfiguration): MutableList<QuestTarget> {
        val questTargetList = mutableListOf<QuestTarget>()
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
            questTargetList.add(target)
        }
        return questTargetList
    }

}