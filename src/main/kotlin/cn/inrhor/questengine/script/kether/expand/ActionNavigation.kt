package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.api.manager.DataManager.navData
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectNavID
import org.bukkit.Location
import taboolib.common.platform.ProxyParticle
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionNavigation {

    class Create(val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                NavData(frame.selectNavID(), it).register(frame.player())
            }
        }
    }

    class Nav(val state: NavData.State, val effect: ProxyParticle = ProxyParticle.VILLAGER_HAPPY): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val sender = frame.player()
            val id = frame.selectNavID()
            val data = sender.uniqueId.getPlayerData()
            val nav = data.navData
            val navData = nav.find { it.id == id }?: return frameVoid()
            when (state) {
                NavData.State.START -> navData.start(sender, effect)
                NavData.State.STOP -> navData.stop(sender)
                else -> {
                    navData.stop(sender)
                    nav.remove(navData)
                }
            }
            return frameVoid()
        }
    }

    companion object {
        @KetherParser(["nav"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("select") {
                    val action = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(action).run<Any>().thenAccept { a ->
                            variables().set("@QenNavID", a.toString())
                        }
                    }
                }
                case("create") {
                    it.mark()
                    it.expect("target")
                    Create(it.next(ArgTypes.ACTION))
                }
                case("set") {
                    when (val state = NavData.State.valueOf(it.nextToken().uppercase())) {
                        NavData.State.START -> {
                            val effect = try {
                                it.mark()
                                it.expect("display")
                                ProxyParticle.valueOf(it.nextToken().uppercase())
                            }catch (ex: Exception) {
                                it.reset()
                                ProxyParticle.VILLAGER_HAPPY
                            }
                            Nav(state, effect)
                        }
                        else -> {
                            Nav(state)
                        }
                    }
                }
                case("stopAll") {
                    actionNow {
                        player().navData().forEach { a ->
                            a.stop(player())
                        }
                    }
                }
            }
        }
    }

}