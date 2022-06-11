package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.database.data.tagsData
import cn.inrhor.questengine.script.kether.player
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class ActionTags {

    class DoTag(val type: Type, val tag: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val tags = frame.player().tagsData()
            if (type == Type.ADD) {
                tags.addTag(tag)
            }else {
                tags.removeTag(tag)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    enum class Type {
        REMOVE, ADD
    }

    class HasTag(val tag: String): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return CompletableFuture<Boolean>().also {
                val tags = frame.player().tagsData()
                it.complete(tags.has(tag))
            }
        }
    }

    /*
     * tags add/remove/has [tag]
     */
    internal object Parser {
        @KetherParser(["tags"])
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