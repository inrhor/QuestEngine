package cn.inrhor.questengine.utlis.time

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

fun Date.toStr(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.format(this)
}

fun String.toDate(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.parse(this)
}

fun Date.toStrYearMM(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM")
    return dateFormat.format(this)
}

/**
 * @return 是否没超时
 */
fun Date.noTimeout(before: Date, after: Date): Boolean {
    return after(before) && before(after)
}

/**
 * 增加日期时间
 */
fun Date.add(timeUnit: Int, add: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(timeUnit, add)
    return calendar.time
}