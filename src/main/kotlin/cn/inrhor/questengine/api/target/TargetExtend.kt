package cn.inrhor.questengine.api.target

import org.bukkit.entity.Player
import taboolib.common.platform.EventPriority
import kotlin.reflect.KClass

abstract class TargetExtend<E: Any> {

    /**
     * 事件名称
     */
    abstract val name: String

    /**
     * 事件
     */
    open var event: KClass<E>? = null

    /**
     * 事件优先级
     */
    open var priority = EventPriority.NORMAL

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