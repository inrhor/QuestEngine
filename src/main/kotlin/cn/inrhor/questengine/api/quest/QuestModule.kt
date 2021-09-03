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
 * @param sort 分类
 */
open class QuestModule {

    var questID: String = ""
    var name: String = ""
    var startInnerQuestID: String = ""
    var modeType: ModeType = ModeType.PERSONAL
    var modeAmount: Int = -1
    var modeShareData: Boolean = true
    var acceptWay: String = ""
    var maxQuantity: Int = 1
    var acceptCheck: Int = -1
    var acceptCondition = mutableListOf<String>()
    var failCheck: Int = -1
    var failCondition = mutableListOf<String>()
    var failKether = mutableListOf<String>()
    var innerQuestList = mutableListOf<QuestInnerModule>()
    var sort: String = ""

    fun getStartInnerQuest(): QuestInnerModule? {
        innerQuestList.forEach {
            if (it.innerQuestID == startInnerQuestID) return it
        }
        return null
    }

}

inline fun buildQuestModule(builder: QuestModule.() -> Unit = {}): QuestModule {
    return QuestModule().also(builder)
}