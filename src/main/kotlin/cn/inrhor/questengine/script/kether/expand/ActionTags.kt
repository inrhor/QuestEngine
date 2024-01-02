package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.api.manager.TagsManager.addTag
import cn.inrhor.questengine.api.manager.TagsManager.clearTag
import cn.inrhor.questengine.api.manager.TagsManager.hasTag
import cn.inrhor.questengine.api.manager.TagsManager.removeTag
import cn.inrhor.questengine.script.kether.player
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionTags {

    class DoTag(val type: Type, val tag: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.player()
            when (type) {
                Type.ADD -> player.addTag(tag)
                Type.REMOVE -> player.removeTag(tag)
                Type.CLEAR -> player.clearTag()
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    enum class Type {
        REMOVE, ADD, CLEAR
    }

    class HasTag(val tag: String): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return CompletableFuture<Boolean>().also {
                it.complete(frame.player().hasTag(tag))
            }
        }
    }

    /*
     * tags add/remove/has [tag]
     */
    internal object Parser {
        @KetherParser(["tags"], shared = true)
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("add", "remove", "has")) {
                "add" -> DoTag(Type.ADD, it.nextToken())
                "remove" -> DoTag(Type.REMOVE, it.nextToken())
                "has" -> HasTag(it.nextToken())
                else -> error("unknown tags")
            }
        }
    }

}