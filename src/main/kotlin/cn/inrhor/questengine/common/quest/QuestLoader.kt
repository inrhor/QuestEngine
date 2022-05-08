package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.target.TargetExtend
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.function.*
import taboolib.common.util.sync

object QuestLoader {

    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.ENABLE)
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
        val ev = event?: return
        registerBukkitListener(ev.java, priority, ignoreCancelled) { e ->
            if (isAsync) {
                tasker(e)?.run{  }
            }else {
                sync {
                    tasker(e)?.run {  }
                }
            }
        }
    }

}