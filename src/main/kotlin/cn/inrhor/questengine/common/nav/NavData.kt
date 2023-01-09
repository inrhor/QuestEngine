package cn.inrhor.questengine.common.nav

import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.module.navigation.NodeEntity
import taboolib.module.navigation.createPathfinder

class NavData(val location: Location, var state: State=State.STOP) {

    enum class State {
        START,STOP,CLEAR
    }

    fun start(player: Player, effect: ProxyParticle) {
        if (state == State.START) return
        state = State.START
        submit(period = 60L) {
            if (state == State.STOP || !player.isOnline) {
                cancel()
                return@submit
            }
            val pLoc = player.location
            val pathFinder = createPathfinder(NodeEntity(pLoc, player.height, player.width))
            val path = pathFinder.findPath(location, distance = 16f)
            path!!.nodes.forEach {
                effect.sendTo(
                    adaptPlayer(player),
                    taboolib.common.util.Location(pLoc.world!!.name, it.x.toDouble(), it.y.toDouble(), it.z.toDouble()))
            }
        }
    }

    fun stop() {
        state = State.STOP
    }

}