package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.RunLogType
import cn.inrhor.questengine.script.kether.runEval
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControlEval(val questID: String, val innerID: String, val id: String, val index: Int): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val controlID = ControlManager.generateControlID(questID, innerID, id)
        val cModule = ControlManager.getControlModule(controlID)
        if (cModule != null) {
            if (ControlManager.runLogType(controlID) != RunLogType.DISABLE) {
                val list = cModule.script
                if (list.size > index) {
                    runEval(player.cast(), list[index])
                }
            }
        }
        return CompletableFuture.completedFuture(null)
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