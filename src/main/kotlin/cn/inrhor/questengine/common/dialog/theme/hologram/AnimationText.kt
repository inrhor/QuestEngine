package cn.inrhor.questengine.common.dialog.theme.hologram

import cn.inrhor.questengine.api.hologram.HoloDisplay
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit

/**
 * 动态全息文本
 */
class AnimationText(content: String) {

    var writeType = WriteType.NORMAL
    var delay = 0L
    var out = false
    var text = ""
    var speed = 0
    var clearWait = 0

    init {
        content.variableReader().forEach {
            if (it == "write") {
                writeType = WriteType.WRITE
            }else if (it == "writeClear") {
                writeType = WriteType.WRITECLEAR
            }else if (it == "out") {
                out = true
            }else if (it.startsWith("delay ")) {
                delay = it.split(" ")[1].toLong()
            }else {
                text = it
            }
        }
    }

    fun sendViewers(holoID: Int, viewers: MutableSet<Player>) {
        if (writeType == WriteType.NORMAL) {
            submit(async = true, delay = this.delay) {
                viewers.forEach {
                    HoloDisplay.updateText(holoID, it, text)
                }
            }
        }else if (writeType == WriteType.WRITECLEAR || writeType == WriteType.WRITE) {
            write()
        }
    }

    private fun write() {

    }

    enum class WriteType {
        NORMAL, WRITE, WRITECLEAR
    }

}