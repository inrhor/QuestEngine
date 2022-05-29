package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.script.kether.player
import nl.pim16aap2.bigDoors.BigDoors
import org.bukkit.Bukkit
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class KetherDoors(val doorID: String, val state: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        if (Bukkit.getPluginManager().getPlugin("BigDoors") == null) return CompletableFuture.completedFuture(null)
        val player = frame.player()
        val com = BigDoors.get()
        val door = com.commander.getDoor(doorID, player)
        val opener = com.getDoorOpener(door.type)
        opener?.openDoor(door, 0.0, state)
        return CompletableFuture.completedFuture(null)
    }

    internal object Parser {
        @KetherParser(["doors"])
        fun parser() = scriptParser {
            it.mark()
            it.expect("to")
            val doorID = it.nextToken()
            val state = try {
                it.mark()
                it.expect("open")
                true
            } catch (ex: Exception) {
                false
            }
            KetherDoors(doorID, state)
        }
    }

}