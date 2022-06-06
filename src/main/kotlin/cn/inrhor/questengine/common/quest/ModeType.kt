package cn.inrhor.questengine.common.quest

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText

enum class ModeType {
    PERSONAL,
    COLLABORATION;

    fun lang(player: Player): String {
        return player.asLangText("MODE-TYPE-$this")
    }
}