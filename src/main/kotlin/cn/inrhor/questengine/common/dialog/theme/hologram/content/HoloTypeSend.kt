package cn.inrhor.questengine.common.dialog.theme.hologram.content

import org.bukkit.entity.Player

/**
 * 全息内容发送接口
 */
interface HoloTypeSend {

    fun sendViewers(holoID: Int, viewers: MutableSet<Player>)

}