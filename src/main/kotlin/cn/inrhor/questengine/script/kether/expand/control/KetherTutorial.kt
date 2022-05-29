package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.script.kether.player
import nl.martenm.servertutorialplus.api.ServerTutorialApi
import org.bukkit.Bukkit
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherTutorial(val id: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (Bukkit.getPluginManager().getPlugin("ServerTutorialPlus") == null) return CompletableFuture.completedFuture(null)
        val player = frame.player()
        val api = ServerTutorialApi.getApi()
        api.startTutorial(id, player)
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {
        @KetherParser(["tutorial"])
        fun parser() = scriptParser {
            it.mark()
            it.expect("start")
            KetherTutorial(it.nextToken())
        }
    }

}