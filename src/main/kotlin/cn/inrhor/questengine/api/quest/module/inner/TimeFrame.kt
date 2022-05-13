package cn.inrhor.questengine.api.quest.module.inner

import org.bukkit.entity.Player
import taboolib.platform.util.asLangText
import java.text.SimpleDateFormat
import java.util.*

class TimeFrame(var type: Type, var duration: String) {

    var start = Date()
    var end: Date? = null

    fun updateTime() {
        start = Date()
        if (type != Type.ALWAYS) {
            val sp = duration.split(">")
            val a = sp[0].split(",")
            val b = sp[1].split(",")
            when (type) {
                Type.DAY -> {
                    val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
                    val ymd= ymdFormat.format(start)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    start = dateFormat.parse("$ymd ${a[0]}")
                    end = dateFormat.parse("$ymd ${b[0]}")
                }
                Type.ALWAYS -> {}
                Type.WEEKLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_WEEK, a[0].toInt()) // 当前周某一天，1是上周日，2是本周一
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.DAY_OF_WEEK, b[0].toInt())
                    val c = a[1].split("-") ;val d = b[1].split("-")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    start = cal1.time
                    end = cal2.time
                }
                Type.MONTHLY -> {
                    val cal1 = Calendar.getInstance()
                    val cal2 = Calendar.getInstance()
                    cal1.set(Calendar.DAY_OF_MONTH, a[0].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[0].toInt())
                    val c = a[1].split("-") ;val d = b[1].split("-")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    start = cal1.time
                    end = cal2.time
                }
                Type.YEARLY -> {
                    val cal1 = Calendar.getInstance()
                    cal1.set(Calendar.MONTH, a[0].toInt()) // 当前年某一月，0是一月
                    val cal2 = Calendar.getInstance()
                    cal2.set(Calendar.MONTH, b[0].toInt())
                    cal1.set(Calendar.DAY_OF_MONTH, a[1].toInt()) // 当前月的某一天
                    cal2.set(Calendar.DAY_OF_MONTH, b[1].toInt())
                    val c = a[2].split("-") ;val d = b[2].split("-")
                    cal1.set(Calendar.HOUR, c[0].toInt());cal1.set(Calendar.MINUTE, c[1].toInt());cal1.set(Calendar.SECOND, c[2].toInt())
                    cal2.set(Calendar.HOUR, d[0].toInt());cal2.set(Calendar.MINUTE, d[1].toInt());cal2.set(Calendar.SECOND, d[2].toInt())
                    start = cal1.time
                    end = cal2.time
                }
                Type.CUSTOM -> {
                    val add = duration.lowercase().split(" ")
                    val cal = Calendar.getInstance()
                    val t = add[1].toInt()
                    when (add[0]) {
                        "s" -> {
                            cal.add(Calendar.SECOND, t)
                        }
                        "m" -> {
                            cal.add(Calendar.MINUTE, t)
                        }
                        "h" -> {
                            cal.add(Calendar.HOUR, t)
                        }
                    }
                    end = cal.time
                }
            }
        }
    }

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