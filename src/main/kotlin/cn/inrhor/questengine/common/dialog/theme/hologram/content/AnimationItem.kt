package cn.inrhor.questengine.common.dialog.theme.hologram.content

import cn.inrhor.questengine.api.dialog.ItemPlay
import cn.inrhor.questengine.utlis.spaceSplit
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

class AnimationItem(content: String): ItemPlay(), HoloTypeSend {

    init {
        content.variableReader().forEach {
            if (it.startsWith("delay ")) {
                delay = it.spaceSplit(1).toLong()
            }else if (it.startsWith("use ")) {
                val type = it.spaceSplit(1).uppercase()
                displayType = Type.valueOf(type)
            }else if (it.startsWith("item ")) {
                itemID = it.spaceSplit(1)
            }
        }
    }

    override fun sendViewers(holoID: Int, viewers: MutableSet<Player>) {
        submit(async = true, delay = this.delay) {

        }
    }

}