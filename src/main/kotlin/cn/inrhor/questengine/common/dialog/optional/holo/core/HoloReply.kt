package cn.inrhor.questengine.common.dialog.optional.holo.core

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.HoloReplyDisplay
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

/**
 * 全息对话回复管理
 */
class HoloReply(
    var replyList: MutableList<ReplyModule>,
    var npcLoc: Location,
    var viewers: MutableSet<Player>,
    val delay: Long) {

    val packetIDs = mutableListOf<Int>()

    fun end() {
        for (id in packetIDs) {
            HoloDisplay.delEntity(id, viewers)
        }
    }

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                for (replyModule in replyList) {
                    runContent(replyModule)
                }
            }
        }.runTaskLaterAsynchronously(QuestEngine.plugin, delay)
    }

    private fun runContent(replyModule: ReplyModule) {
        var holoLoc = npcLoc
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        for (viewer in viewers) {
            val pData = DataStorage().getPlayerData(viewer)
            pData.dialogData.holoReplyList.add(this)
        }
        for (i in replyModule.content) {
            val iUc = i.uppercase(Locale.getDefault())
            when {
                iUc.startsWith("HITBOX") -> {
                    val fixedHoloHitBox = KetherHandler.evalHoloHitBox(i)
                    val boxLoc = LocationTool().getFixedHoloBoxLoc(npcLoc, fixedHoloHitBox)
                    val holoHitBox = HoloHitBox(replyModule, boxLoc, fixedHoloHitBox, viewers)
                    holoHitBox.viewBox()
                    for (viewer in viewers) {
                        val pData = DataStorage.playerDataStorage[viewer.uniqueId]?: return
                        val holoBoxList = pData.dialogData.holoBoxList
                        if (!holoBoxList.equals(holoHitBox)) {
                            holoBoxList.add(holoHitBox)
                        }
                    }
                }
                iUc.startsWith("INITLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(npcLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    holoLoc = LocationTool().getFixedLoc(holoLoc, KetherHandler.evalFixedLoc(i))
                }
                iUc.startsWith("NEXTY") -> {
                    val get = i.substring(i.indexOf(" ")+1)
                    nextY = get.toDouble()
                }
                iUc.startsWith("TEXT") -> {
                    val textDisplay = replyModule.textList[textIndex]
                    val text = textDisplay.text
                    val holoID = textDisplay.holoID
                    textIndex++
                    holoLoc = holoLoc.add(0.0, nextY, 0.0)
                    HoloReplyDisplay().text(holoID, viewers, holoLoc, text)
                    packetIDs.add(holoID)
                }
                iUc.startsWith("ITEM") -> {
                    val itemDisplay = replyModule.itemList[itemIndex]
                    val holoID = itemDisplay.holoID
                    val itemID = itemDisplay.itemID
                    val item = itemDisplay.item
                    itemIndex++
                    HoloReplyDisplay().item(holoID, itemID, viewers ,holoLoc, item)
                    packetIDs.add(holoID)
                    packetIDs.add(itemID)
                }
            }
        }
    }

}