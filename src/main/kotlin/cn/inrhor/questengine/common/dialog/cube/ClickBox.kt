package cn.inrhor.questengine.common.dialog.cube

import org.bukkit.Location

class ClickBox(private val boxLoc: Location, private val x: Double, private val y: Double, private val z: Double) {

    fun isClick(clickLoc: Location): Boolean {
        val boxLoc1 = boxLoc.add(-x, -y, -z)
        val boxLoc2 = boxLoc.add(x, y, z)
        val cuboid = Cuboid(boxLoc1, boxLoc2)
        return cuboid.contains(clickLoc)
    }

}