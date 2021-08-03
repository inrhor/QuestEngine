package cn.inrhor.questengine.utlis.time

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.common.quest.QuestTarget
import org.bukkit.entity.Player
import taboolib.common.platform.console
import taboolib.module.lang.asLangText
import taboolib.platform.util.asLangText
import java.util.*
import java.util.Calendar

object TimeUtil {

    fun addDate(date: Date, timeUnit: Int, add: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(timeUnit, add)
        return calendar.time
    }

    /**
     * 时间单位
     */
    fun timeUnitLang(targetData: TargetData): String {
        when (targetData.timeUnit.lowercase(Locale.getDefault())) {
            "s" -> return console().asLangText("QUEST-TIME_S")
            "minute" -> return console().asLangText("QUEST-TIME_MINUTE")
        }
        return ""
    }

    /**
     * 获得时间标识符
     */
    fun timeUnit(target: QuestTarget): String {
        val str = target.time.lowercase(Locale.getDefault())
        if (str == "always") {
            return ""
        }
        return str.split(" ")[0]
    }

    fun remainDate(player: Player, future: Date): String {
        val nowDate = Date()
        val i = future.time - nowDate.time
        val day = i / (24 * 60 * 60 * 1000)
        val hour = i / (60 * 60 * 1000) - day * 24
        val minute = i / (60 * 1000) - day * 24 * 60 - hour * 60
        val second = i / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60
        val d = player.asLangText("QUEST-TIME_DAY")
        val h = player.asLangText("QUEST-TIME_HOUR")
        val m = player.asLangText("QUEST-TIME_MINUTE")
        val s = player.asLangText("QUEST-TIME_S")
        return "$day$d$hour$h$minute$m$second$s"
    }

}