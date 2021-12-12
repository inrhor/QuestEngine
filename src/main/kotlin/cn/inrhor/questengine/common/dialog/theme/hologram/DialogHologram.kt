package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.DialogTheme
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationItem
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationText
import cn.inrhor.questengine.common.dialog.theme.hologram.content.HoloTypeSend
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * 全息对话
 */
class DialogHologram(
    val dialogModule: DialogModule,
    val viewers: MutableSet<Player>,
    val location: Location): DialogTheme {

    val origin = OriginLocation(location)
    private val holoData = HologramData()

    /**
     * 向可视者播放全息对话
     */
    override fun play() {
        parserContent(origin)
    }

    /**
     * 解析对话内容
     */
    private fun parserContent(origin: OriginLocation) {
        dialogModule.dialog.forEach {
            val u = it.lowercase()
            if (u.startsWith("initloc ")) {
                origin.reset(it)
            }else if (u.startsWith("addloc ")) {
                origin.add(it)
            }else if (u.startsWith("nexty")) {
                origin.nextY = it.split(" ")[1].toDouble()
            }else if (u.startsWith("text")) {
                sendViewers(AnimationText(it))
            }else if (u.startsWith("item ")) {
                sendViewers(AnimationItem(it))
            }
        }
    }

    private fun sendViewers(holoDialogSend: HoloTypeSend) {
        val holoID = HoloIDManager.generate(
            dialogModule.dialogID, holoData.size(), HoloIDManager.Type.TEXT)
        holoData.create(holoID, viewers, origin)
        holoDialogSend.sendViewers(holoID, viewers)
    }

}