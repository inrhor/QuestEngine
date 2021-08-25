package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.quest.manager.QuestManager
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherInnerQuest {

    class DoQuest(val type: Type, val questID: String, val innerID: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
//            if (type == Type.ACCEPT) {
                QuestManager.acceptInnerQuest(player.cast(), questID, innerID, true)
//            }
            return CompletableFuture.completedFuture(null)
        }
    }

    enum class Type {
        ACCEPT, FINISH
    }

    class ChangeQuest(val type: Type, val questID: String, val innerID: String, val state: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
//            if (type == Type.FINISH) {
            QuestManager.finishInnerQuest(player.cast(), questID, innerID)
//            }
            return CompletableFuture.completedFuture(null)
        }
    }

    /*
     * InnerQuest accept [questID] [innerQuestID]
     * InnerQuest finish [questID] [innerQuestID] [state]
     */
    internal object Parser {
        @KetherParser(["innerquest"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("accept", "finish")) {
                "accept" -> DoQuest(Type.ACCEPT, it.nextToken(), it.nextToken())
                "finish" -> ChangeQuest(Type.FINISH, it.nextToken(), it.nextToken(), it.nextToken())
                else -> error("unknown InnerQuest")
            }
        }
    }

}