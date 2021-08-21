package cn.inrhor.questengine.common.dialog.optional.holo.core

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationItem
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationText
import cn.inrhor.questengine.script.kether.evalReferLoc
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * 全息主体对话管理
 */
class HoloDialog(
    var dialogModule: DialogModule,
    var npcLoc: Location,
    var viewers: MutableSet<Player>) {

    var endDialog = false

    private val packetIDs = mutableListOf<Int>()

    fun end() {
        endDialog = true
        for (id in packetIDs) {
            destroyEntity(viewers, id)
        }
        // 在 HoloReply 清理 viewers
    }

    fun run() {
        var holoLoc = npcLoc.clone()
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        val dialogID = dialogModule.dialogID
        viewers.forEach {
            DataStorage.getPlayerData(it).dialogData.addHoloDialog(dialogID, this)
        }
        for (i in dialogModule.dialog) {
            val iUc = i.uppercase()
            when {
                iUc.startsWith("INITLOC") -> {
                    holoLoc = LocationTool.getReferLoc(npcLoc, evalReferLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    holoLoc.yaw = npcLoc.clone().yaw
                    holoLoc = LocationTool.getReferLoc(holoLoc, evalReferLoc(i))
                }
                iUc.startsWith("NEXTY") -> {
                    val get = i.substring(i.indexOf(" ")+1)
                    nextY = get.toDouble()
                }
                iUc.startsWith("TEXT") -> {
                    val playText = dialogModule.playText[textIndex]
                    textIndex++
                    holoLoc.add(0.0, nextY, 0.0)
                    HoloAnimationText(this, viewers, playText, holoLoc).runTask()
                    packetIDs.add(playText.holoID)
                }
                iUc.startsWith("ITEMWRITE") -> {
                    val playItem = dialogModule.playItem[itemIndex]
                    val holoID = playItem.holoID
                    itemIndex++
                    holoLoc.add(0.0, nextY, 0.0)

                    /*
                    *  为何不放入HoloAnimationItem
                    *  别问了，holoLoc有毛病
                    */
                    spawnAS(viewers, holoID, holoLoc)
                    HoloDisplay.initItemAS(holoID, viewers)

                    HoloAnimationItem(this, viewers, playItem, holoLoc).run()

                    packetIDs.add(playItem.holoID)
                    packetIDs.add(playItem.itemID)
                }
                iUc.startsWith("REPLY") -> { // 弹出回复选项
                    val replyList = dialogModule.replyModuleList
                    val get = i.substring(i.indexOf(" ")+1)
                    val delay = get.toLong()
                    HoloReply(replyList, npcLoc, viewers, delay).run()
                }
            }
        }
    }
}