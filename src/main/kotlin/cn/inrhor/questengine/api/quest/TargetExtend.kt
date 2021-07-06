package cn.inrhor.questengine.api.quest

import org.bukkit.entity.Player
import org.bukkit.event.Event
import kotlin.reflect.KClass

abstract class TargetExtend<E: Event> {

    /**
     * 事件名称
     */
    abstract val name: String

    /**
     * 事件
     */
    abstract val event: KClass<E>

    /**
     * 时间
     */
    abstract var time: String

    /**
     * 完成奖励
     */
    abstract var finishReward: String

    /**
     * 目标进度
     */
    abstract var schedule: Int

    /**
     * 事件的玩家
     */
    var tasker: ((E) -> Player?) = { null }

    protected fun tasker(handle: E.() -> Player?) {
        this.tasker = handle
    }

    /**
     * 事件的条件
     */
    private val conditionList = mutableMapOf<String, ConditionType>()

    fun addCondition(content: String, conditionType: ConditionType) {
        conditionList[content] = conditionType
    }

}