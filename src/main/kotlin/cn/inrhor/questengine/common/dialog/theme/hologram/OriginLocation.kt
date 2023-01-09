package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.utlis.location.LocationTool
import cn.inrhor.questengine.utlis.location.builderReferLoc
import org.bukkit.Location

/**
 * 全息源位置
 */
class OriginLocation(private val location: Location, var nextY: Double = 0.0) {

    /**
     * 源位置
     */
    var origin = location.clone()

    /**
     * 从最初始位置进行定义源位置
     */
    fun reset(content: String) {
        origin = location.clone()
        add(content)
    }

    /**
     * 更新源位置
     */
    fun add(content: String) {
        val referLoc = builderReferLoc(content)
        origin = LocationTool.getReferLoc(origin.yaw, origin, referLoc)
    }

}