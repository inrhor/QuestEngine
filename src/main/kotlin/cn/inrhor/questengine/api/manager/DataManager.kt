package cn.inrhor.questengine.api.manager

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.StorageData
import cn.inrhor.questengine.common.database.data.TagsData
import cn.inrhor.questengine.common.database.data.quest.QuestData
import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.common.quest.enum.StateType
import org.bukkit.entity.Player

object DataManager {

    /**
     * @return 玩家队伍
     */
    fun Player.teamData(): TeamOpen? {
        return getPlayerData().teamData
    }

    /**
     * @return 是否存在任务数据
     */
    fun Player.existQuestData(questID: String): Boolean {
        return getPlayerData().dataContainer.quest.containsKey(questID)
    }

    /**
     * @return 任务数据
     */
    fun Player.questData(questID: String): QuestData? {
        return getPlayerData().dataContainer.quest[questID]
    }

    /**
     * @return 标签数据
     */
    fun Player.tagsData(): TagsData {
        return getPlayerData().dataContainer.tags
    }

    /**
     * @return 自定义数据集
     */
    fun Player.storage(): MutableList<StorageData> {
        return getPlayerData().dataContainer.storage
    }

    /**
     * @return 自定义数据
     */
    fun Player.getStorageValue(key: String): String {
        storage().forEach {
            if (it.key == key) return it.value
        }
        return "null"
    }

    /**
     * 设置自定义数据
     */
    fun Player.setStorage(key: String, value: String) {
        storage().forEach {
            if (it.key == key) {
                it.value = value
                return
            }
        }
        storage().add(StorageData(key, value))
    }

    /**
     * 删除某些自定义数据
     */
    fun Player.delStorage(key: String) {
        val s = storage().iterator()
        while (s.hasNext()) {
            if (s.next().key == key) s.remove()
            break
        }
    }

    /**
     * @return 目标数据
     */
    fun Player.targetData(questID: String, targetID: String): TargetData? {
        questData(questID)?.target?.forEach {
            if (it.id == targetID) return it
        }
        return null
    }

    /**
     * @return 是否完成任务的所有目标
     */
    fun Player.completedTargets(questID: String, modeType: ModeType): Boolean {
        if (modeType == ModeType.COLLABORATION) {
            teamData()?.playerMembers()?.forEach {
                if (!it.completedTarget(questID)) return false
            }
        }else {
            return completedTarget(questID)
        }
        return true
    }

    /**
     * @return 是否完成任务的所有目标
     */
    fun Player.completedTarget(questID: String): Boolean {
        questData(questID)?.target?.forEach {
            if (it.state != StateType.FINISH) return false
        }
        return true
    }

    /**
     * 正在进行的任务目标条目列
     */
    fun Player.doingTargets(name: String): List<TargetData> {
        val list = mutableListOf<TargetData>()
        val doing = StateType.DOING
        getPlayerData().dataContainer.quest.values.forEach {
            if (it.state == doing) {
                it.target.forEach { t ->
                    if (t.state == doing && t.getTargetFrame()?.event == name) list.add(t)
                }
            }
        }
        return list
    }

}