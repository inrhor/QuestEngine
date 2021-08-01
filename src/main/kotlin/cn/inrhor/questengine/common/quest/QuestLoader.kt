package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.TargetExtend
import cn.inrhor.questengine.common.quest.manager.TargetManager
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.registerListener
import java.util.*

object QuestLoader {

    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.ACTIVE)
    fun registerTarget() {
        runningClasses.forEach {
            if (TargetExtend::class.java.isAssignableFrom(it)) {
                (it.getInstance() as? TargetExtend<Event>)?.register()
            }
        }
    }

    fun <T : Event> TargetExtend<T>.register() {
        registerListener(event.java, priority, ignoreCancelled) { e ->
            tasker(e)?.run {
                TargetManager.targetMap.values.forEach {
                    if (isAsync) {
                        if (!it.check()) return@forEach
                    }else {
                        if (!it.check()) return@forEach
                    }
                }
            }
        }
    }

}