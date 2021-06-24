package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.utlis.public.MsgUtil
import org.bukkit.Location
import org.bukkit.entity.Player

class HoloHitBox(val dialogID: String, val replyID: String,
                 val x: Double, val y: Double, val z: Double,
                 val sizeMinX: Double, val sizeMinY: Double, val sizeMinZ: Double,
                 val sizeMaxX: Double, val sizeMaxY: Double, val sizeMaxZ: Double,
                 val itemID: String, val itemY: Double) {

    private fun isBox(viewLoc: Location, boxLoc: Location): Boolean {
        val minX = boxLoc.x-sizeMinX
        val maxX = boxLoc.x+sizeMaxX
        val minY = boxLoc.y-sizeMinY
        val maxY = boxLoc.y+sizeMaxY
        val minZ = boxLoc.z-sizeMinZ
        val maxZ = boxLoc.z+sizeMaxZ
        val x = viewLoc.x
        val y = viewLoc.y
        val z = viewLoc.z
        if (x in minX..maxX && y in minY..maxY && z in minZ..maxZ ) {
            return true
        }
        return false
    }

    fun isBox(player: Player, boxLoc: Location) {
        val eyeLoc = player.eyeLocation
        val viewLoc = eyeLoc.clone()
        val dir = eyeLoc.direction
        val long = 5
        while (viewLoc.distance(eyeLoc) <= long) {
            viewLoc.add(dir)
            MsgUtil.send("isBox  "+isBox(viewLoc, boxLoc))
            if (isBox(viewLoc, boxLoc)) break
        }
    }

}