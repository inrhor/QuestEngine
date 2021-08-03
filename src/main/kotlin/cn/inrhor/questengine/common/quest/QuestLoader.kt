package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.registerListener
import taboolib.common.util.sync
import java.util.*

object QuestLoader {

    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.ACTIVE)
    fun registerTarget() {
        runningClasses.forEach {
            if (TargetExtend::class.java.isAssignableFrom(it) && TargetExtend::class.java != it) {
                val i = it.getInstance(false)?.get()
                val ev = i as? TargetExtend<Event>
                ev?.register()
            }
        }
    }

    fun <T : Event> TargetExtend<T>.register() {
        registerListener(event.java, priority, ignoreCancelled) { e ->
            val event = name
            tasker(e)?.run {
                TargetManager.targetMap.forEach { (eventMeta, condition) ->
                    if (eventMeta.split("-")[0] == event) {
                        if (isAsync) {
                            var next = true
                            sync {
                                if (!condition.check()) {
                                    next = false; return@sync
                                }
                            }
                            if (!next) return@run
                        } else {
                            if (!condition.check()) return@run
                        }
                    }
                }
            }
        }
    }

}