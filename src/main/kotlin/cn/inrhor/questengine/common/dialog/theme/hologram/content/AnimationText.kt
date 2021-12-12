package cn.inrhor.questengine.common.dialog.theme.hologram.content

import cn.inrhor.questengine.api.dialog.TextPlay
import cn.inrhor.questengine.api.packet.updateDisplayName
import cn.inrhor.questengine.utlis.spaceSplit
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed
import taboolib.platform.compat.replacePlaceholder

/**
 * 动态全息文本
 */
class AnimationText(val content: String): TextPlay() {

    var writeType = WriteType.NORMAL
    var out = false
    var clearWait = 0L

    init {
        content.variableReader().forEach {
            if (it == "write") {
                writeType = WriteType.WRITE
            }else if (it == "writeClear") {
                writeType = WriteType.WRITECLEAR
            }else if (it == "out") {
                out = true
            }else if (it.startsWith("delay ")) {
                delay = it.spaceSplit(1).toLong()
            }else if (it.startsWith("speed ")) {
                speed = it.spaceSplit(1).toLong()
            }else if (it.startsWith("clearWait ")) {
                clearWait = it.spaceSplit(1).toLong()
            }else {
                text = it
            }
        }
    }

    fun sendViewers(holoID: Int, viewers: MutableSet<Player>) {
        submit(async = true, delay = this.delay) {
            if (viewers.isEmpty()) {
                cancel(); return@submit
            }
            if (writeType == WriteType.NORMAL) {
                viewers.forEach {
                    updateDisplayName(it, holoID, text)
                }
            } else if (writeType == WriteType.WRITECLEAR || writeType == WriteType.WRITE) {
                write(holoID, viewers)
            }
        }
    }

    private fun write(holoID: Int, viewers: MutableSet<Player>) {
        viewers.forEach {
            val animationList = text.printed().replacePlaceholder(it)
            if (animationList.isEmpty()) return
            updateDisplayName(it, holoID, animationList[0])
            writeSpeed(it, holoID, 1, animationList)
        }
    }

    private fun writeSpeed(viewer: Player, holoID: Int, index: Int, animationList: List<String>) {
        if (index >= animationList.size) return
        submit(async = true, delay = speed) {
            if (viewer.isOnline) {
                updateDisplayName(viewer, holoID, animationList[index])
                writeSpeed(viewer, holoID, index + 1, animationList)
            }
        }
    }

    enum class WriteType {
        NORMAL, WRITE, WRITECLEAR
    }

}