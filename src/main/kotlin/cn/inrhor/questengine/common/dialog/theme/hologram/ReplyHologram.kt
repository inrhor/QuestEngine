package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.DialogTheme
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.updateDisplayName
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationItem
import cn.inrhor.questengine.utlis.location.ReferHoloHitBox
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 全息回复
 */
class ReplyHologram(
    val dialogID: String,
    val viewers: MutableSet<Player>,
    val reply: MutableList<ReplyModule>,
    val delay: Long,
    val location: Location,
    val holoData: HologramData): DialogTheme {

    val origin = OriginLocation(location)

    /**
     * 播放回复
     */
    override fun play() {
        submit(async = true, delay = this.delay) {
            if (viewers.isEmpty()) {
                cancel()
                return@submit
            }
            reply.forEach{
                parserContent(it)
            }
        }
    }

    private fun parserContent(replyModule: ReplyModule) {
        replyModule.content.forEach {
            val u = it.lowercase()
            if (u.startsWith("text ")) {
                text(replyModule, it.variableReader()[0])
            }else if (u.startsWith("item ")) {
                item(replyModule, it)
            }else if (u.startsWith("hitbox ")) {
                hitBox(replyModule, it).sendHitBox(origin)
            }else {
                parserOrigin(origin, it)
            }
        }
    }

    private fun text(replyModule: ReplyModule, text: String) {
        val type = HoloIDManager.Type.TEXT
        val holoID = HoloIDManager.generate(
            dialogID, replyModule.replyID,
            holoData.size(), type)
        holoData.create(holoID, viewers, origin, type)
        updateDisplayName(viewers, holoID, text)
    }

    private fun item(replyModule: ReplyModule, content: String) {
        val index = holoData.size()
        val type = HoloIDManager.Type.ITEM
        val replyID = replyModule.replyID
        val itemHoloID = HoloIDManager.generate(
            dialogID, replyID, index, type)
        val stackHoloID = HoloIDManager.generate(
            dialogID, replyID, index+1, HoloIDManager.Type.ITEMSTACK)
        holoData.create(itemHoloID, viewers, origin, type)
        val animation = AnimationItem(content, holoData)
        animation.sendViewers(viewers, origin, itemHoloID, stackHoloID)
    }

    private fun hitBox(replyModule: ReplyModule, content: String): ReferHoloHitBox {
        val index = holoData.size()
        val replyID = replyModule.replyID
        val hitBoxID = HoloIDManager.generate(
            dialogID, replyID, index, HoloIDManager.Type.HITBOX)
        val stackID = HoloIDManager.generate(
            dialogID, replyID, index+1, HoloIDManager.Type.ITEMSTACK)
        return ReferHoloHitBox(content, hitBoxID, stackID)
    }

    override fun end() {

    }

    override fun addViewer(viewer: Player) {

    }

    override fun deleteViewer(viewer: Player) {

    }
}