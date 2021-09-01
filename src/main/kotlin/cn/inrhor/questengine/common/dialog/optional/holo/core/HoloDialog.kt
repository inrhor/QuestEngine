package cn.inrhor.questengine.common.dialog.optional.holo.core

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationItem
import cn.inrhor.questengine.common.dialog.optional.holo.HoloAnimationText
import cn.inrhor.questengine.script.kether.evalReferLoc
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player




/**
 * 全息主体对话管理
 */
class HoloDialog(
    var dialogModule: DialogModule,
    var npcLoc: Location,
    var viewers: MutableSet<Player>) {

    var endDialog = false

    private val packetIDs = mutableSetOf<Int>()

    fun end() {
        endDialog = true
        for (id in packetIDs) {
            destroyEntity(viewers, id)
        }
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
        val yaw = npcLoc.clone().yaw
        for (i in dialogModule.dialog) {
            val iUc = i.uppercase()
            when {
                iUc.startsWith("INITLOC") -> {
                    holoLoc = LocationTool.getReferLoc(yaw, npcLoc, evalReferLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    holoLoc = LocationTool.getReferLoc(yaw, npcLoc, evalReferLoc(i))
                }
                iUc.startsWith("NEXTY") -> {
                    val get = i.substring(i.indexOf(" ")+1)
                    nextY = get.toDouble()
                }
                iUc.startsWith("TEXT") -> {
                    holoLoc.add(0.0, nextY, 0.0)
                    viewers.forEach {
                        DialogManager.animation(dialogID, it)
                        val playText = dialogModule.playText[textIndex]
                        HoloAnimationText(this, it, playText, holoLoc).runTask()
                        packetIDs.add(playText.holoID)
                    }
                    textIndex++
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
                iUc.startsWith("REPLYALL") -> { // 弹出全部回复选项
                    val replyList = dialogModule.replyModuleList
                    val sp = i.split(" ")
                    val delay = sp[1].toLong()
                    HoloReply(replyList, npcLoc, viewers, delay).run()
                }
                iUc.startsWith("REPLY") -> { // 弹出指定回复选项
                    val sp = i.split(" ")
                    val delay = sp[1].toLong()
                    val id = sp[2]
                    dialogModule.replyModuleList.forEach {
                        if (it.replyID == id) {
                            HoloReply(mutableListOf(it), npcLoc, viewers, delay).run()
                        }
                    }
                }
            }
        }
    }
}