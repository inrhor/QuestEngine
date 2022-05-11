package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.script.kether.expand.editor.frameVoid
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionNav() {

    class Create(val id: String, val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                val sender = frame.script().sender as? ProxyPlayer ?: error("unknown player")
                DataStorage.getPlayerData(sender.uniqueId).navData[id] = NavData(it)
            }
        }
    }

    class Nav(val id: String, val state: NavData.State, val effect: ProxyParticle = ProxyParticle.VILLAGER_HAPPY): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val sender = frame.script().sender as? ProxyPlayer ?: error("unknown player")
            val p = sender.cast<Player>()
            val data = DataStorage.getPlayerData(sender.uniqueId)
            val nav = data.navData
            if (nav.containsKey(id)) {
                val n = data.navData[id]!!
                when (state) {
                    NavData.State.START -> n.start(p, effect)
                    NavData.State.STOP -> n.stop()
                    else -> {
                        n.stop()
                        nav.remove(id)
                    }
                }
            }
            return frameVoid()
        }
    }

    /**
     * nav create [id] target world 99 9 66
     * nav set [id] to stop/clear
     * nav set [id] to start effect [effect]
     */
    companion object {
        @KetherParser(["nav"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("create") {
                    val id = it.nextToken()
                    it.mark()
                    it.expect("target")
                    Create(id, it.next(ArgTypes.ACTION))
                }
                case("set") {
                    val id = it.nextToken()
                    it.expect("to")
                    when (val state = NavData.State.valueOf(it.nextToken().uppercase())) {
                        NavData.State.START -> {
                            val effect = try {
                                it.mark()
                                it.expect("effect")
                                ProxyParticle.valueOf(it.nextToken().uppercase())
                            }catch (ex: Exception) {
                                it.reset()
                                ProxyParticle.VILLAGER_HAPPY
                            }
                            Nav(id, state, effect)
                        }
                        else -> {
                            Nav(id, state)
                        }
                    }
                }
            }
        }
    }

}