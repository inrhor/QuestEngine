package cn.inrhor.questengine.common.dialog.cube

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.atan

class ClickBox(val boxLoc: Location, val radius: Double) {

    fun isClick(player: Player): Boolean {
        val direction = Vector(0, 0, 0)
        val distance = player.eyeLocation.distance(this.boxLoc)
        direction.add(this.boxLoc.toVector().subtract(player.eyeLocation.toVector()))
        return (direction.angle(player.location.direction) <= atan(radius / distance))
    }

}