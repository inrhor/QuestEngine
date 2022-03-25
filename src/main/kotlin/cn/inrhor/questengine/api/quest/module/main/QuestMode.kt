package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.common.quest.ModeType
import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

class QuestMode(
    var type: ModeType,
    var amount: Int,
    var shareData: Boolean) {

    constructor(): this(ModeType.PERSONAL, -1, false)

    fun modeTypeLang(player: Player): String =
        if (type == ModeType.PERSONAL) player.asLangText("MODE-TYPE-PERSONAL")
        else player.asLangText("MODE-TYPE-COLLABORATION")

}