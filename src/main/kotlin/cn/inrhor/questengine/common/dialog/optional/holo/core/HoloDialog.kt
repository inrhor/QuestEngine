package cn.inrhor.questengine.common.dialog.optional.holo.core

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationItem
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationText
import cn.inrhor.questengine.common.kether.KetherHandler
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
            HoloDisplay.delEntity(id, viewers)
        }
    }

    fun run() {
        var holoLoc = npcLoc
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        for (viewer in viewers) {
            val pData = DataStorage().getPlayerData(viewer)?: return
            pData.dialogData.holoDialogList.add(this)
        }
        for (i in dialogModule.dialog) {
            val iUc = i.uppercase(Locale.getDefault())
            when {
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
                    val playText = dialogModule.playText[textIndex]
                    textIndex++
                    holoLoc.add(0.0, nextY, 0.0)
                    HoloAnimationText(this, viewers, playText, holoLoc).runTask()
                    packetIDs.add(playText.holoID)
                }
                iUc.startsWith("ITEMNORMAL") -> {
                    val playItem = dialogModule.playItem[itemIndex]
                    val holoID = playItem.holoID
                    itemIndex++
                    holoLoc.add(0.0, nextY, 0.0)

                    /*
                    *  为何不放入HoloAnimationItem
                    *  别问了，holoLoc有毛病
                    */
                    HoloDisplay.spawnAS(holoID, viewers, holoLoc)
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