package cn.inrhor.questengine.api.quest.module.inner

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
import java.text.SimpleDateFormat
import java.util.*

class TimeFrame(var type: Type, var duration: String) {

    constructor():this(Type.ALWAYS, "")

    enum class Type {
        ALWAYS, DAY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    }

    fun lang(player: Player): String {
        val sp = duration.split(">")
        val a = sp[0].split(",")
        val b = sp[1].split(",")
        return when (type) {
            Type.DAY -> {
                player.asLangText("TIME-FRAME-DAY",
                    a[0],b[0])
            }
            Type.WEEKLY -> {
                player.asLangText("TIME-FRAME-WEEKLY",
                    player.asLangText("TIME-FRAME-WEEK-${a[0]}"), a[1],
                    player.asLangText("TIME-FRAME-WEEK-${b[0]}"), b[1])
            }
            Type.MONTHLY -> {
                player.asLangText("TIME-FRAME-MONTHLY",
                    a[0],a[1],b[0],b[1])
            }
            Type.YEARLY -> {
                player.asLangText("TIME-FRAME-YEARLY",
                    a[0],a[1],a[2],b[0],b[1],b[2])
            }
            else -> player.asLangText("QUEST-ALWAYS")
        }
    }

    fun noTimeout(now: Date, before: Date, after: Date): Boolean {
        return now.after(before) && now.before(after)
    }

}