package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.api.quest.ControlPriority
import cn.inrhor.questengine.api.quest.toControlPriority
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.script.kether.eval
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControlEval(val questID: String, val innerID: String, val priority: String, val index: Int): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val controlID = ControlManager.generateControlID(questID, innerID, priority)
        val cModule = ControlManager.getControlModule(controlID)
        if (cModule != null) {
            val pri = priority.toControlPriority()
            if (ControlManager.runLogType(controlID, pri) != RunLogType.DISABLE) {
                when (pri) {
                    ControlPriority.HIGHEST -> {
                        shellEval(player, cModule.highestControl, index)
                    }
                    ControlPriority.NORMAL -> {
                        shellEval(player, cModule.normalControl, index)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    private fun shellEval(player: ProxyPlayer, list: MutableList<String>, index: Int) {
        if (list.size > index) {
            eval(player.cast(), list[index])
        }
    }

    /*
     * control @this index [index]
     */
    internal object Parser {

        @KetherParser(["control"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val questID = it.nextToken()
            val innerID = it.nextToken()
            val priority = it.nextToken()
            it.mark()
            it.expect("index")
            KetherControlEval(questID, innerID, priority, it.nextInt())
        }
    }

}