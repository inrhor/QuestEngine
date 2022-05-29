package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.script.kether.player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherTags {

    class DoTag(val type: Type, val tag: String): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.player()
            val pData = DataStorage.getPlayerData(player.uniqueId)
            val tags = pData.tagsData
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
                val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                val pData = DataStorage.getPlayerData(player.uniqueId)
                it.complete(pData.tagsData.has(tag))
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