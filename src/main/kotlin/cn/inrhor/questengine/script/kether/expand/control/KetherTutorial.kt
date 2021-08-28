package cn.inrhor.questengine.script.kether.expand.control

import nl.martenm.servertutorialplus.api.ServerTutorialApi
import nl.pim16aap2.bigDoors.BigDoors
import org.bukkit.Bukkit
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherTutorial(val id: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (Bukkit.getPluginManager().getPlugin("ServerTutorialPlus") == null) return CompletableFuture.completedFuture(null)
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val api = ServerTutorialApi.getApi()
        api.startTutorial(id, player.cast())
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