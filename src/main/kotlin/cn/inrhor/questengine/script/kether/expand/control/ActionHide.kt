package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.script.kether.expand.editor.frameVoid
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.library.kether.ArgTypes
import taboolib.module.kether.*
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.CompletableFuture

class ActionHide(val hide: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val sender = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val player = sender.cast<Player>()
        Bukkit.getOnlinePlayers().forEach {
            if (hide) {
                if (MinecraftVersion.majorLegacy >= 11300) {
                    it.hidePlayer(QuestEngine.plugin, player)
                } else {
                    it.hidePlayer(player)
                }
            }else {
                if (MinecraftVersion.majorLegacy >= 11300) {
                    it.showPlayer(QuestEngine.plugin, player)
                } else {
                    it.showPlayer(player)
                }
            }
        }
        return frameVoid()
    }

    /**
     * hide player set (true/false)
     */
    companion object {
        @KetherParser(["hide"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("player") {
                    it.mark()
                    it.expect("set")
                    val set = it.nextToken()=="true"
                    ActionHide(set)
                }
            }
        }
    }
}