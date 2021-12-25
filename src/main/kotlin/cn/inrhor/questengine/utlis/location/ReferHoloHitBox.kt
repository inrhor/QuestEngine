package cn.inrhor.questengine.utlis.location

import cn.inrhor.questengine.api.dialog.ItemPlay
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.destroyEntity
import cn.inrhor.questengine.common.dialog.theme.hologram.HologramData
import cn.inrhor.questengine.common.dialog.theme.hologram.OriginLocation
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @param long 距离
 * @param boxY 显示物品的高度
 */
data class ReferHoloHitBox(
    val content: String,
    val hitBoxID: Int,
    val stackID: Int,
    var offset: Float = 0F,
    var multiply: Double = 0.0,
    var height: Double = 0.0,
    var long: Double = 0.0,
    var itemStack: ItemStack = ItemStack(Material.STONE),
    var type: ItemPlay.Type = ItemPlay.Type.FIXED,
    var boxY: Double = 0.0,
    var hitBox: BoundingBox = BoundingBox.initHitBox()) {

    // 是否已经初始化数据包
    var initItem = false

    fun sendHitBox(origin: OriginLocation) {
        content.variableReader().forEach {
            val u = it.lowercase()
            val sp = it.split(" ")
            if (u.startsWith("sizex ")) {
                hitBox.minX = origin.origin.x-sp[1].toDouble()
                hitBox.maxX = origin.origin.x+sp[2].toDouble()
            }else if (u.startsWith("sizey ")) {
                hitBox.minY = origin.origin.y-sp[1].toDouble()
                hitBox.maxY = origin.origin.y+sp[2].toDouble()
            }else if (u.startsWith("sizez ")) {
                hitBox.minZ = origin.origin.z-sp[1].toDouble()
                hitBox.maxZ = origin.origin.z+sp[2].toDouble()
            }else if (u.startsWith("long ")) {
                long = sp[1].toDouble()
            }else if (u.startsWith("item ")) {
                itemStack = ItemManager.get(sp[1])
            }else if (u.startsWith("use ")) {
                type = ItemPlay.Type.valueOf(sp[1])
            }else if (u.startsWith("boxy ")) {
                boxY = sp[1].toDouble()
            }
            val loc = origin.origin
            hitBox = hitBox.move(loc.x, loc.y, loc.z)
        }
    }

    fun sendViewItem(viewer: Player, origin: OriginLocation, holoData: HologramData) {
        val p = mutableSetOf(viewer)
        if (initItem) {
            holoData.create(hitBoxID, p, origin, HoloIDManager.Type.HITBOX)
            return
        }
        if (type == ItemPlay.Type.SUSPEND) {
            HoloDisplay.passengerItem(hitBoxID, stackID, p, origin.origin, itemStack)
        }else {
            HoloDisplay.equipHeadItem(hitBoxID, p, itemStack)
        }
    }

    fun pause(viewer: Player) {
        destroyEntity(viewer, hitBoxID)
        destroyEntity(viewer, stackID)
    }

    fun endHitBox(viewers: MutableSet<Player>) {
        viewers.forEach {
            pause(it)
            // 删除数据
        }
    }
}