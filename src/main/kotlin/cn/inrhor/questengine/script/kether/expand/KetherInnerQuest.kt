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
            if (type == Type.ACCEPT) {
                QuestManager.acceptInnerQuest(player.cast(), questID, innerID, true)
            }else if (type == Type.FINISH) {
                QuestManager.finishInnerQuest(player.cast(), questID, innerID)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    enum class Type {
        ACCEPT, FINISH
    }

    /*
     * InnerQuest accept/finish [questID] [innerQuestID]
     */
    internal object Parser {
        @KetherParser(["innerquest"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("accept", "finish")) {
                "accept" -> DoQuest(Type.ACCEPT, it.nextToken(), it.nextToken())
                "finish" -> DoQuest(Type.FINISH, it.nextToken(), it.nextToken())
                else -> error("unknown InnerQuest")
            }
        }
    }

}