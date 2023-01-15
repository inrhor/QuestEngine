package cn.inrhor.questengine.common.nav

import cn.inrhor.questengine.api.manager.DataManager.getNavAllData
import cn.inrhor.questengine.utlis.location.LocationTool
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
        player.getNavAllData().forEach { it.stop() }
        state = State.START
        submit(period = 20L, async = true) {
            if (state == State.STOP || !player.isOnline || LocationTool.inLoc(player.location, location, 2.0, 2.0, 2.0)) {
                state = State.STOP
                cancel()
                return@submit
            }
            val pLoc = player.location
            val pathFinder = createPathfinder(NodeEntity(pLoc, player.height, player.width))
            val path = pathFinder.findPath(location, distance = 16f)
            path!!.nodes.forEach {
                effect.sendTo(
                    adaptPlayer(player),
                    taboolib.common.util.Location(pLoc.world!!.name, it.x.toDouble(), it.y.toDouble()+1, it.z.toDouble()))
            }
        }
    }

    fun stop() {
        state = State.STOP
    }

}