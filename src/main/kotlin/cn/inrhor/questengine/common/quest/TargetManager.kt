package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.TargetExtend
import org.bukkit.configuration.file.FileConfiguration
import java.util.HashMap
import java.util.LinkedHashMap

class TargetManager {

    companion object {
        /**
         * 注册的任务目标
         */
        private var questTargetMap: HashMap<String, TargetExtend<*>> = LinkedHashMap()
    }

    fun register(event: String, target: TargetExtend<*>) {
        questTargetMap[event] = target
    }

    fun getTargetList(yaml: FileConfiguration): MutableList<TargetExtend<*>> {
        val targetList = mutableListOf<TargetExtend<*>>()
        for (i in yaml.getConfigurationSection("target")!!.getKeys(false)) {
            val s = "target.$i."
            val event = yaml.getString(s+"event")
            if (questTargetMap.contains(event)) {
                val target = questTargetMap[event]!!
                target.finishReward = yaml.getString(s+"reward")!!
                target.time = yaml.getString(s+"time")!!
            }
        }
        return targetList
    }

}