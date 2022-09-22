package cn.inrhor.questengine.script.kether.expand.hook

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import org.bukkit.Location
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionGuard {

    class GuardQuery(val loc: ParsedAction<*>, val id: ParsedAction<*>): ScriptAction<Boolean>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
            return frame.newFrame(loc).run<Location>().thenApply { l ->
                frame.newFrame(id).run<String>().thenApply { i ->
                    val container = WorldGuard.getInstance().platform.regionContainer
                    val regs = container.get(BukkitAdapter.adapt(l.world))
                    val reg = regs?.getRegion(i)
                    reg?.contains(l.blockX, l.blockY, l.blockZ)
                }.join()
            }
        }
    }

    companion object {
        @KetherParser(["worldguard"], shared = true)
        fun parser() = scriptParser {
            val any = it.next(ArgTypes.ACTION)
            when (it.expects("id")) {
                "id" -> {
                    val id = it.next(ArgTypes.ACTION)
                    GuardQuery(any, id)
                }
                else -> error("worldguard ")
            }
        }
    }

}