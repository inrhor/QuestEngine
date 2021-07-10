package cn.inrhor.questengine.common.dialog.optional.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.item.ItemManager
import cn.inrhor.questengine.utlis.location.FixedHoloHitBox
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HoloHitBox(val replyModule: ReplyModule,
                 val boxLoc: Location,
                 val fixedHoloHitBox: FixedHoloHitBox,
                 var viewers: MutableSet<Player>) {

    private val packetIDs = mutableListOf<Int>()
    private var task: BukkitRunnable? = null

    fun end() {
        task?.cancel()
        for (id in packetIDs) {
            HoloDisplay.delEntity(id, viewers)
        }
    }

    private fun isBox(viewLoc: Location): Boolean {
        val minX = boxLoc.x-fixedHoloHitBox.minX
        val maxX = boxLoc.x+fixedHoloHitBox.maxX
        val minY = boxLoc.y-fixedHoloHitBox.minY
        val maxY = boxLoc.y+fixedHoloHitBox.maxY
        val minZ = boxLoc.z-fixedHoloHitBox.minZ
        val maxZ = boxLoc.z+fixedHoloHitBox.maxZ
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
            val long = fixedHoloHitBox.long
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
        val item = ItemManager.get(fixedHoloHitBox.itemID)
        packetIDs.add(holoID)
        packetIDs.add(itemID)
        task = object : BukkitRunnable() {
            override fun run() {
                if (viewers.isEmpty())  {
                    cancel(); return
                }
                if (isBox()) {
                    if (!spawnHolo) {
                        HoloDisplay.spawnAS(holoID, viewers, boxLoc)
                        HoloDisplay.initItemAS(holoID, viewers)
                        HoloDisplay.updateItem(holoID, itemID, viewers, boxLoc, item)
                        spawnHolo = true
                        displayItem = true
                    }else if (!displayItem) {
                        HoloDisplay.updateItem(holoID, itemID, viewers, boxLoc, item)
                        displayItem = true
                    }
                }else {
                    displayItem = false
                    HoloDisplay.delEntity(itemID, viewers)
                }
            }
        }
        (task as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 5L)
    }

}