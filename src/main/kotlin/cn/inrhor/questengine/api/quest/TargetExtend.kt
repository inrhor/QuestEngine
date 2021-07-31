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
     * 事件优先级
     */
    open var priority = 0

    /**
     * 事件忽略已取消
     */
    open val ignoreCancelled = true

    /**
     * 是否异步执行
     */
    open val isAsync = false

    /**
     * 事件的玩家
     */
    var tasker: ((E) -> Player?) = { null }
        private set

    protected fun tasker(handle: E.() -> Player?) {
        this.tasker = handle
    }

}