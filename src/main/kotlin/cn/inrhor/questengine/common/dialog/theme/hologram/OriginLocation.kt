package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Location

/**
 * 全息源位置
 *
 * @param location 必须传入 location#clone
 */
class OriginLocation(private val location: Location, var nextY: Double = 0.0) {

    /**
     * 源位置
     */
    private var origin = location.clone()

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
        content.variableReader().forEach {
            val u = it.lowercase()
            val dir = ""
            val m = 0
            val o = 0
            if (u.startsWith("dir ")) {

            }else if (u.startsWith("add ")) {

            }
        }
    }

}