package cn.inrhor.questengine.utlis.location

import org.bukkit.Location
import taboolib.library.kether.QuestReader
import taboolib.module.kether.KetherError
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation
import java.util.*

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
        return when (q.nextToken().lowercase(Locale.getDefault())) {
            "left" -> -90F
            "right" -> 90F
            "behind" -> 180F
            else -> throw KetherError.CUSTOM.create("未知方向类型")
        }
    }
}