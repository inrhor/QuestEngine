package cn.inrhor.questengine.common.database.data.quest

import cn.inrhor.questengine.common.quest.QuestTarget

/**
 * 任务目标存储
 */
class TargetData(val name: String, var time: Int, var schedule: Int, val questTarget: QuestTarget) {
}