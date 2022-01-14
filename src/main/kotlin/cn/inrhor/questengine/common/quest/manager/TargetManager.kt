package cn.inrhor.questengine.common.quest.manager

import cn.inrhor.questengine.api.target.ConditionType
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.ModeType
import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.utlis.ui.BuilderFrame
import cn.inrhor.questengine.utlis.ui.NoteComponent
import cn.inrhor.questengine.utlis.ui.buildFrame
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.module.configuration.Configuration

object TargetManager {

    val targetMap = mutableMapOf<String, ConditionType>()

    /**
     * 规范配置
     */
    fun register(name: String, meta: String): TargetManager {
        set(name, meta, ConditionType(meta))
        return this
    }

    fun register(name: String, meta: String, list: MutableList<String>): TargetManager {
        set(name, meta, ConditionType(list))
        return this
    }

    fun set(name: String, meta: String, conditionType: ConditionType): TargetManager {
        targetMap["$name-$meta"] = conditionType
        return this
    }

    fun getTargetList(yaml: Configuration): MutableMap<String, QuestTarget> {
        val questTargetList = mutableMapOf<String, QuestTarget>()
        for (i in yaml.getConfigurationSection("inner.target")!!.getKeys(false)) {
            val s = "inner.target.$i."
            val name = yaml.getString(s + "name") ?: "null"
            val time = yaml.getString(s + "time") ?: "always"
            val reward = yaml.getString(s + "reward") ?: "null"
            val condition = mutableMapOf<String, String>()
            val conditionList = mutableMapOf<String, List<String>>()

            val ui = buildFrame()

            targetMap.forEach { (eventNameMeta, conditionType) ->
                val eventName = eventNameMeta.split("-")[0]
                if (eventName == name) {
                    val path = "inner.target.$i"
                    for (node in yaml.getConfigurationSection(path)!!.getKeys(true)) {
                        val u = "$path.$node"
                        if (node == "ui" || node == "description") {
                            if (node == "description") {
                                ui.noteComponent[u] = NoteComponent(yaml.getStringList(u).toMutableList())
                            }else {
                                ui.sectionAdd(yaml, u, BuilderFrame.Type.CUSTOM)
                            }
                        }else {
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
            }
            val period = yaml.getInt(s+"period")
            val async = yaml.getBoolean(s+"async")
            val conditions = yaml.getStringList(s+"conditions")
            val target = QuestTarget(name, time, reward, period, async, conditions,
                condition, conditionList, ui)
            questTargetList[name] = target
        }
        return questTargetList
    }

    /**
     * 计算任务目标进度，支持协同模式
     */
    fun scheduleUtil(name: String, questData: QuestData, targetData: TargetData): Int {
        val questModule = QuestManager.getQuestModule(questData.questID)?: return 0
        val mode = questModule.mode
        if (mode.modeType() == ModeType.COLLABORATION && mode.shareData && questData.teamData != null) {
            var schedule = 0
            for (mUUID in questData.teamData!!.members) {
                val m = Bukkit.getPlayer(mUUID)?: continue
                val innerData = QuestManager.getInnerQuestData(m, questData.questUUID)?: continue
                val tgData = innerData.targetsData[name]?: continue
                schedule += tgData.schedule
            }
            return schedule
        }
        if (questModule.mode.modeType() == ModeType.PERSONAL) {
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