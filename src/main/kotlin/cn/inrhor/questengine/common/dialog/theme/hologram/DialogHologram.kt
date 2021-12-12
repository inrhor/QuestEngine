package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.DialogTheme
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationItem
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationText
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
                textViewers(it)
            }else if (u.startsWith("item ")) {
                itemViewers(it)
            }
        }
    }

    /**
     * 向 viewer 发送文本
     */
    private fun textViewers(text: String) {
        val holoID = HoloIDManager.generate(
            dialogModule.dialogID, holoData.size(), HoloIDManager.Type.TEXT)
        holoData.create(holoID, viewers, origin, HoloIDManager.Type.TEXT)
        val animation = AnimationText(text)
        animation.sendViewers(holoID, viewers)
    }

    /**
     * 向 viewers 发送物品
     */
    private fun itemViewers(content: String) {
        val dialogID = dialogModule.dialogID
        val index = holoData.size()
        val itemHoloID = HoloIDManager.generate(
            dialogID, index, HoloIDManager.Type.ITEM)
        val stackHoloID = HoloIDManager.generate(
            dialogID, index+1, HoloIDManager.Type.ITEMSTACK)
        holoData.create(itemHoloID, viewers, origin, HoloIDManager.Type.ITEM)
        val animation = AnimationItem(content, holoData)
        animation.sendViewers(viewers, origin, itemHoloID, stackHoloID)
    }

}