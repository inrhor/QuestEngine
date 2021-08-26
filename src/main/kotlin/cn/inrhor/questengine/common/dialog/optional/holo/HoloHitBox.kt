package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.animation.item.ItemDialogPlay
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.location.ReferHoloHitBox
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.*
import taboolib.common.platform.service.PlatformExecutor

class HoloHitBox(val replyModule: ReplyModule,
                 val boxLoc: Location,
                 val referHoloHitBox: ReferHoloHitBox,
                 var viewers: MutableSet<Player>) {

    private val packetIDs = mutableListOf<Int>()
    private var task: PlatformExecutor.PlatformTask? = null

    var end = false

    fun end() {
        end = true
        packetIDs.forEach {
            destroyEntity(viewers, it)
        }
    }

    private fun isBox(viewLoc: Location): Boolean {
        val minX = boxLoc.x-referHoloHitBox.minX
        val maxX = boxLoc.x+referHoloHitBox.maxX
        val minY = boxLoc.y-referHoloHitBox.minY
        val maxY = boxLoc.y+referHoloHitBox.maxY
        val minZ = boxLoc.z-referHoloHitBox.minZ
        val maxZ = boxLoc.z+referHoloHitBox.maxZ
        val x = viewLoc.x
        val y = viewLoc.y
        val z = viewLoc.z
        if (x in minX..maxX && y in minY..maxY && z in minZ..maxZ ) {
            return true
        }
        return false
    }

    fun isBox(): Boolean {
        for (player in viewers) {
            val eyeLoc = player.eyeLocation
            val viewLoc = eyeLoc.clone()
            val dir = eyeLoc.direction
            val long = referHoloHitBox.long
            while (viewLoc.distance(eyeLoc) <= long) {
                viewLoc.add(dir)
                if (isBox(viewLoc)) return true
            }
        }
        return false
    }

    fun viewBox() {
        var spawnHolo = false
        var displayItem = false
        val dialogID = replyModule.dialogID
        val replyID = replyModule.replyID
        val holoID = HoloIDManager.generate(dialogID, replyID, 0, "hitBox")
        val itemID = HoloIDManager.generate(dialogID, replyID, 1, "hitBox")
        val item = ItemManager.get(referHoloHitBox.itemID)
        val type = referHoloHitBox.type
        packetIDs.add(holoID)
        packetIDs.add(itemID)
        task = submit(async = true, period = 5L) {
            if (viewers.isEmpty() || end)  {
                cancel(); return@submit
            }
            if (isBox()) {
                val boxY = referHoloHitBox.boxY
                val itemLoc = boxLoc.clone().add(0.0, boxY, 0.0)
                if (!spawnHolo) {
                    spawnAS(viewers, holoID, itemLoc)
                    HoloDisplay.initItemAS(holoID, viewers)
                    displayItem(holoID, itemID, viewers, itemLoc, item, type)
                    spawnHolo = true
                    displayItem = true
                }else if (!displayItem) {
                    displayItem(holoID, itemID, viewers, itemLoc, item, type)
                    displayItem = true
                }
            }else {
                displayItem = false
                endDisplayItem(holoID, itemID, viewers, type)
            }
        }
    }

    fun displayItem(holoID: Int, itemID: Int, viewers: MutableSet<Player>, itemLoc: Location, item: ItemStack, type: ItemDialogPlay.Type) {
        if (type == ItemDialogPlay.Type.SUSPEND) {
            HoloDisplay.updateItem(holoID, itemID, viewers, itemLoc, item)
        }else {
            HoloDisplay.equipHeadItem(holoID, viewers, item)
        }
    }

    fun endDisplayItem(holoID: Int, itemID: Int, viewers: MutableSet<Player>, type: ItemDialogPlay.Type) {
        if (type == ItemDialogPlay.Type.SUSPEND) {
            destroyEntity(viewers, itemID)
        }else {
            HoloDisplay.equipHeadItem(holoID, viewers, ItemStack(Material.AIR))
        }
    }

}