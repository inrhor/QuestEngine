package cn.inrhor.questengine.common.packet

import org.bukkit.Location

/**
 * 玩家数据包数据
 *
 * 主要应用于删除实体数据包
 */
class DataPacket(val packetID: String, val entityID: Int, val location: Location) {

}