package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.DialogTheme
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.utlis.variableReader
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
            }
        }
    }

    /**
     * 向 viewer 发送文本
     * 支持 PAPI
     */
    private fun textViewers(text: String) {
        holoData.create(HoloIDManager.generate(
            dialogModule.dialogID, holoData.size(), HoloIDManager.Type.TEXT))

        viewers.forEach {

        }
    }

    /**
     * 解析文本
     */
    private fun parserText(text: String) {
        text.variableReader().forEach {

        }
    }

}