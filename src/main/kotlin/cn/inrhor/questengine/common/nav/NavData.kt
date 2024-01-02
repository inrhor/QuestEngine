package cn.inrhor.questengine.common.nav

import cn.inrhor.questengine.api.event.data.NavigationDataEvent
import cn.inrhor.questengine.api.manager.DataManager.navData
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.module.navigation.NodeEntity
import taboolib.module.navigation.createPathfinder

class NavData(val id: String, val location: Location, var state: State=State.STOP) {

    enum class State(val int: Int) {
        START(0),
        STOP(1),
        CLEAR(2);

        companion object {
            private val values = State.values()
            fun fromInt(value: Int) = values.firstOrNull { it.int == value }?: STOP
        }
    }

    fun start(player: Player, effect: ProxyParticle) {
        if (state == State.START) return
        player.navData().forEach { if (it.state == State.START) it.stop(player) }
        state = State.START
        NavigationDataEvent.UpdateState(player, this).call()
        submit(period = 20L, async = true) {
            if (state == State.STOP || !player.isOnline || LocationTool.inLoc(
                    player.location, location, 2.0, 2.0, 2.0)) {
                stop(player)
                cancel()
                return@submit
            }
            val pLoc = player.location
            val pathFinder = createPathfinder(NodeEntity(pLoc, player.height, player.width))
            val path = pathFinder.findPath(location, distance = 16f)
            path!!.nodes.forEach {
                effect.sendTo(
                    adaptPlayer(player),
                    taboolib.common.util.Location(
                        pLoc.world!!.name, it.x.toDouble(), it.y.toDouble()+1, it.z.toDouble()))
            }
        }
    }

    fun stop(player: Player) {
        state = State.STOP
        NavigationDataEvent.UpdateState(player, this).call()
    }

    fun register(player: Player) {
        player.navData().add(this)
    }

    fun unload(player: Player) {
        NavigationDataEvent.Remove(player, this).call()
        player.navData().remove(this)
    }

}