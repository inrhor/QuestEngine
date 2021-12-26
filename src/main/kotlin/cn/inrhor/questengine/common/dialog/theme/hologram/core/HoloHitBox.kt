package cn.inrhor.questengine.common.dialog.theme.hologram.core

import cn.inrhor.questengine.api.dialog.theme.ItemPlay
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.destroyEntity
import cn.inrhor.questengine.common.dialog.theme.hologram.HitBoxData
import cn.inrhor.questengine.common.dialog.theme.hologram.HologramData
import cn.inrhor.questengine.common.dialog.theme.hologram.OriginLocation
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit

/**
 * @param long 距离
 * @param boxY 显示物品的高度
 */
data class HoloHitBox(
    val dialogHolo: DialogHologram,
    val replyModule: ReplyModule,
    val content: String,
    val hitBoxID: Int,
    val stackID: Int,
    val hitBoxData: HitBoxData = HitBoxData()
) {

    // 是否已经初始化数据包
    var initItem = false

    fun sendHitBox(origin: OriginLocation) {
        content.variableReader().forEach {
            val u = it.lowercase()
            val sp = it.split(" ")
            if (u.startsWith("sizex ")) {
                hitBoxData.hitBox.minX = origin.origin.x-sp[1].toDouble()
                hitBoxData.hitBox.maxX = origin.origin.x+sp[2].toDouble()
            }else if (u.startsWith("sizey ")) {
                hitBoxData.hitBox.minY = origin.origin.y-sp[1].toDouble()
                hitBoxData.hitBox.maxY = origin.origin.y+sp[2].toDouble()
            }else if (u.startsWith("sizez ")) {
                hitBoxData.hitBox.minZ = origin.origin.z-sp[1].toDouble()
                hitBoxData.hitBox.maxZ = origin.origin.z+sp[2].toDouble()
            }else if (u.startsWith("long ")) {
                hitBoxData.long = sp[1].toDouble()
            }else if (u.startsWith("item ")) {
                hitBoxData.itemStack = ItemManager.get(sp[1])
            }else if (u.startsWith("use ")) {
                hitBoxData.type = ItemPlay.Type.valueOf(sp[1])
            }else if (u.startsWith("boxy ")) {
                hitBoxData.boxY = sp[1].toDouble()
            }
            val loc = origin.origin
            hitBoxData.hitBox = hitBoxData.hitBox.move(loc.x, loc.y, loc.z)
        }
    }

    fun taskView(viewers: MutableSet<Player>, origin: OriginLocation, holoData: HologramData) {
        submit(async = true, period = 5L) {
            if (viewers.isEmpty())  {
                cancel(); return@submit
            }
            viewers.forEach {
                if (isBox(it)) {
                    sendViewItem(it, origin, holoData)
                }else {
                    pause(it)
                }
            }
        }
    }

    fun isBox(viewer: Player): Boolean {
        val eyeLoc = viewer.eyeLocation
        val viewLoc = eyeLoc.clone()
        val dir = eyeLoc.direction
        while (viewLoc.distance(eyeLoc) <= hitBoxData.long) {
            viewLoc.add(dir)
            if (hitBoxData.hitBox.contains(viewLoc.toVector())) return true
        }
        return false
    }

    fun sendViewItem(viewer: Player, origin: OriginLocation, holoData: HologramData) {
        val p = mutableSetOf(viewer)
        if (initItem) {
            holoData.create(hitBoxID, p, origin, HoloIDManager.Type.HITBOX)
            return
        }
        val itemStack = hitBoxData.itemStack
        if (hitBoxData.type == ItemPlay.Type.SUSPEND) {
            HoloDisplay.passengerItem(hitBoxID, stackID, p, origin.origin, itemStack)
        }else {
            HoloDisplay.equipHeadItem(hitBoxID, p, itemStack)
        }
    }

    fun pause(viewer: Player) {
        if (hitBoxData.type == ItemPlay.Type.SUSPEND) {
            destroyEntity(viewer, stackID)
        }else {
            HoloDisplay.equipHeadItem(hitBoxID, mutableSetOf(viewer), ItemStack(Material.AIR))
        }
    }

    fun endHitBox(viewers: MutableSet<Player>) {
        destroyEntity(viewers, hitBoxID)
        destroyEntity(viewers, stackID)
        viewers.forEach {
            pause(it)
            // 删除数据
        }
    }
}