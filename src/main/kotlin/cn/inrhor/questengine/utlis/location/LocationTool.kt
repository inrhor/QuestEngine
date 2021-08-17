package cn.inrhor.questengine.utlis.location

import org.bukkit.Location
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

object LocationTool {

    fun getReferLoc(ownLoc: Location, referLoc: ReferLocation): Location {
        return ownLoc.toProxyLocation().referTo(
            referLoc.offset, referLoc.multiply, referLoc.height)
            .toBukkitLocation()
    }

    fun getReferHoloBoxLoc(ownLoc: Location, referHoloHitBox: ReferHoloHitBox): Location {
        return ownLoc.toProxyLocation().referTo(
            referHoloHitBox.offset, referHoloHitBox.multiply, referHoloHitBox.height)
            .toBukkitLocation()
    }

    fun getOffsetType(q: QuestReader): Float {
        return when (q.nextToken().lowercase()) {
            "left" -> -90F
            "right" -> 90F
            "behind" -> 180F
            else -> throw KetherError.CUSTOM.create("未知方向类型")
        }
    }

    fun inLoc(fromLoc: Location, targetLoc: Location, range: Double): Boolean {
        val x = fromLoc.x
        val y = fromLoc.y
        val z = fromLoc.z
        val tX = targetLoc.x
        val tY = targetLoc.y
        val tZ = targetLoc.z
        val r = if (range == 0.0) 1.0 else range
        val minX = tX-r
        val maxX = tX+r
        val minY = tY-r
        val maxY = tY+r
        val minZ = tZ-r
        val maxZ = tZ+r
        if (x in minX..maxX && y in minY..maxY && z in minZ..maxZ ) {
            return true
        }
        return false
    }
}