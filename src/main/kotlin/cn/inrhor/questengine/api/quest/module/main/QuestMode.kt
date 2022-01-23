package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.common.quest.ModeType
import org.bukkit.entity.Player
import taboolib.library.configuration.PreserveNotNull
import taboolib.platform.util.asLangText

@PreserveNotNull
class QuestMode(val type: String, val amount: Int, val shareData: Boolean) {

    constructor(): this("PERSONAL", -1, false)

    fun modeType(): ModeType = ModeType.valueOf(type.uppercase())

    fun modeTypeLang(player: Player): String =
        if (modeType() == ModeType.PERSONAL) player.asLangText("MODE-TYPE-PERSONAL")
        else player.asLangText("MODE-TYPE-COLLABORATION")

}