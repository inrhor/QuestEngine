package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Location

/**
 * 解析源位置字符
 */
class LocationParser {

    /**
     * 得到已解析的源位置
     */
    fun getLocation(location: Location, content: String): Location {
        val loc = location.clone()
        content.variableReader().forEach {
            if () {

            }
        }
        return loc
    }

}