package cn.inrhor.questengine.common.dialog.optional.holo.core

import cn.inrhor.questengine.api.packet.*
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.optional.holo.HoloHitBox
import cn.inrhor.questengine.common.dialog.optional.holo.HoloReplyDisplay
import cn.inrhor.questengine.script.kether.evalHoloHitBox
import cn.inrhor.questengine.script.kether.evalReferLoc
import cn.inrhor.questengine.utlis.location.LocationTool
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.*
import java.util.*

/**
 * 全息对话回复管理
 */
class HoloReply(
    var replyList: MutableList<ReplyModule>,
    var npcLoc: Location,
    var viewers: MutableSet<Player>,
    val delay: Long) {

    private val packetIDs = mutableListOf<Int>()

    fun end() {
        packetIDs.forEach {
            destroyEntity(viewers, it)
        }
        viewers.clear()
    }

    fun run() {
        submit(async = true, delay = this.delay) {
            if (viewers.isEmpty()) {
                cancel()
                return@submit
            }
            for (replyModule in replyList) {
                runContent(replyModule)
            }
        }
    }

    private fun runContent(replyModule: ReplyModule) {
        var holoLoc = npcLoc
        var nextY = 0.0
        var textIndex = 0
        var itemIndex = 0
        val dialogID = replyModule.dialogID
        viewers.forEach {
            DataStorage.getPlayerData(it).dialogData.addHoloReply(dialogID, this)
        }
        for (i in replyModule.content) {
            val iUc = i.uppercase()
            when {
                iUc.startsWith("HITBOX") -> {
                    val referHoloHitBox = evalHoloHitBox(i)
                    val boxLoc = LocationTool.getReferHoloBoxLoc(npcLoc, referHoloHitBox)
                    val holoHitBox = HoloHitBox(replyModule, boxLoc, referHoloHitBox, viewers)
                    holoHitBox.viewBox()
                    for (viewer in viewers) {
                        DataStorage.getPlayerData(viewer).dialogData.addHoloBox(dialogID, holoHitBox)
                    }
                }
                iUc.startsWith("INITLOC") -> {
                    holoLoc = LocationTool.getReferLoc(npcLoc, evalReferLoc(i))
                }
                iUc.startsWith("ADDLOC") -> {
                    holoLoc = LocationTool.getReferLoc(holoLoc, evalReferLoc(i))
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