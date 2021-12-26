package cn.inrhor.questengine.common.dialog.theme.hologram.core

import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.dialog.theme.ReplyTheme
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.api.packet.updateDisplayName
import cn.inrhor.questengine.common.dialog.theme.hologram.OriginLocation
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationItem
import cn.inrhor.questengine.common.dialog.theme.hologram.parserOrigin
import cn.inrhor.questengine.utlis.variableReader
import taboolib.common.platform.function.submit

/**
 * 全息回复
 */
class ReplyHologram(
    val dialogHolo: DialogHologram,
    val reply: MutableList<ReplyModule>,
    val delay: Long,
    val holoHitBox: HoloHitBox = HoloHitBox()
): ReplyTheme {

    val origin = OriginLocation(dialogHolo.npcLoc)

    /**
     * 播放回复
     */
    override fun play() {
        submit(async = true, delay = this.delay) {
            if (dialogHolo.viewers.isEmpty()) {
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
        val holoData = dialogHolo.holoData
        val holoID = HoloIDManager.generate(
            dialogHolo.dialogModule.dialogID, replyModule.replyID,
            holoData.size(), type)
        holoData.create(holoID, dialogHolo.viewers, origin, type)
        updateDisplayName(dialogHolo.viewers, holoID, text)
    }

    private fun item(replyModule: ReplyModule, content: String) {
        val holoData = dialogHolo.holoData
        val index = holoData.size()
        val type = HoloIDManager.Type.ITEM
        val replyID = replyModule.replyID
        val dialogID = dialogHolo.dialogModule.dialogID
        val itemHoloID = HoloIDManager.generate(
            dialogID, replyID, index, type)
        val stackHoloID = HoloIDManager.generate(
            dialogID, replyID, index+1, HoloIDManager.Type.ITEMSTACK)
        holoData.create(itemHoloID, dialogHolo.viewers, origin, type)
        val animation = AnimationItem(content, holoData)
        animation.sendViewers(dialogHolo.viewers, origin, itemHoloID, stackHoloID)
    }

    private fun hitBox(replyModule: ReplyModule, content: String): HitBoxSpawner {
        val index = dialogHolo.holoData.size()
        val replyID = replyModule.replyID
        val dialogID = dialogHolo.dialogModule.dialogID
        val hitBoxID = HoloIDManager.generate(
            dialogID, replyID, index, HoloIDManager.Type.HITBOX)
        val stackID = HoloIDManager.generate(
            dialogID, replyID, index+1, HoloIDManager.Type.ITEMSTACK)
        val hitBox = HitBoxSpawner(dialogHolo, replyModule, content, hitBoxID, stackID)
        holoHitBox.hitBoxList.add(hitBox)
        return hitBox
    }

    override fun end() {
        // 暂无可操作
    }
}