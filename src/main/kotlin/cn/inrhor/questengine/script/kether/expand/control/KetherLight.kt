package cn.inrhor.questengine.script.kether.expand.control

import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.nms.createLight
import taboolib.module.nms.deleteLight
import taboolib.module.nms.type.LightType
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

class KetherLight {

    class CreateLight(val level: Int, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                it.block.createLight(level, LightType.ALL, true)
            }
        }
    }

    class DeleteLight(val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                it.block.deleteLight(LightType.ALL, true)
            }
        }
    }

    internal object Parser {
        @KetherParser(["light"])
        fun parser() = scriptParser {
            it.mark()
            when (it.expects("create", "delete")) {
                "create" -> {
                    it.mark()
                    it.expect("level")
                    val level = it.nextInt()
                    it.mark()
                    it.expect("where")
                    CreateLight(level, it.next(ArgTypes.ACTION))
                }
                "delete" -> {
                    it.mark()
                    it.expect("where")
                    DeleteLight(it.next(ArgTypes.ACTION))
                }
                else -> error("unknown type")
            }

        }
    }

}