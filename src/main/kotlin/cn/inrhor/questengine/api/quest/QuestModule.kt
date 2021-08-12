package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.quest.ModeType

/**
 * 任务模块
 *
 * @param name 任务名称
 * @param startInnerQuestID 开始的内部任务ID
 * @param modeType 任务成员模式
 * @param modeAmount 任务成员人数
 * @param modeShareData 是否分享任务数据
 * @param acceptWay 任务自动化设置
 * @param maxQuantity 接受上限
 * @param acceptCheck 接受条件数
 * @param acceptCondition 接受条件
 * @param failCheck 失败条件数
 * @param failCondition 失败条件
 * @param failKether 失败脚本
 * @param innerQuestList 内部任务集
 */
class QuestModule(val questID: String,
                  var name: String,
                  var startInnerQuestID: String,
                  var modeType: ModeType,
                  var modeAmount: Int,
                  var modeShareData: Boolean,
                  var acceptWay: String,
                  var maxQuantity: Int,
                  var acceptCheck: Int, var acceptCondition: MutableList<String>,
                  var failCheck: Int, var failCondition: MutableList<String>,
                  var failKether: MutableList<String>,
                  var innerQuestList: MutableList<QuestInnerModule>) {

    fun getStartInnerQuest(): QuestInnerModule? {
        innerQuestList.forEach {
            if (it.innerQuestID == startInnerQuestID) return it
        }
        return null
    }

}