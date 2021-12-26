package cn.inrhor.questengine.utlis.location

import cn.inrhor.questengine.common.dialog.theme.hologram.core.HoloHitBox
import org.bukkit.Location
import taboolib.module.kether.*
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

object LocationTool {

    fun getReferLoc(yaw: Float, ownLoc: Location, referLoc: ReferLocation): Location {
        return ownLoc.toProxyLocation().referTo(yaw,
            referLoc.offset, referLoc.multiply, referLoc.height)
            .toBukkitLocation()
    }

    fun getReferHoloBoxLoc(yaw: Float, ownLoc: Location, referHoloHitBox: HoloHitBox): Location {
        return ownLoc.toProxyLocation().referTo(yaw,
            referHoloHitBox.offset, referHoloHitBox.multiply, referHoloHitBox.height)
            .toBukkitLocation()
    }

    fun getOffsetType(dir: String): Float {
        return when (dir.lowercase()) {
            "left" -> -90F
            "right" -> 90F
            "behind" -> 180F
            else -> throw KetherError.CUSTOM.create("未知方向类型")
        }
    }

    fun inLoc(fromLoc: Location, targetLoc: Location, range: Double): Boolean {
        val x = fromLoc.blockX
        val y = fromLoc.blockY
        val z = fromLoc.blockZ
        val tX = targetLoc.blockX
        val tY = targetLoc.blockY
        val tZ = targetLoc.blockZ
        val r = if (range == 0.0) 1.0 else range
        val minX = tX-r
        val maxX = tX+r
        val minY = tY-r
        val maxY = tY+r
        val minZ = tZ-r
        val maxZ = tZ+r
        if (x.toDouble() in minX..maxX && y.toDouble() in minY..maxY && z.toDouble() in minZ..maxZ ) {
            return true
        }
        return false
    }

    fun inLoc(fromLoc: Location, targetLoc: Location, rangeX: Double, rangeY: Double, rangeZ: Double): Boolean {
        val x = fromLoc.blockX
        val y = fromLoc.blockY
        val z = fromLoc.blockZ
        val tX = targetLoc.blockX
        val tY = targetLoc.blockY
        val tZ = targetLoc.blockZ
        val minX = tX-rangeX
        val maxX = tX+rangeX
        val minY = tY-rangeY
        val maxY = tY+rangeY
        val minZ = tZ-rangeZ
        val maxZ = tZ+rangeZ
        if (x.toDouble() in minX..maxX && y.toDouble() in minY..maxY && z.toDouble() in minZ..maxZ ) {
            return true
        }
        return false
    }
}