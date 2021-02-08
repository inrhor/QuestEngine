package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.Dialog
import cn.inrhor.questengine.common.dialog.animation.FrameWrite
import cn.inrhor.questengine.common.dialog.animation.UtilAnimation
import cn.inrhor.questengine.common.hologram.IHolo
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class DialogHolo(
    var holo: IHolo,
    var viewers: MutableSet<Player>,
    var runnable: BukkitRunnable?
) {

    constructor(holo: IHolo, viewers: MutableSet<Player>) : this(holo, viewers, null)

    fun runRunnable() {

        // 主动态帧
        var frame = 0
        val dialogFile = Dialog().getDialog(holo.holoID)!!

        val frameWriteMap = mutableMapOf<Int, MutableList<FrameWrite>>()
        val isCancelsMap = mutableMapOf<Int, MutableList<Boolean>>()

        for (line in 0 until dialogFile.ownTextContent!!.size) {
            val theLineFrameWriteList = mutableListOf<FrameWrite>()
            repeat(dialogFile.getOwnTheLineList(line).size) {
                val frameWrite = FrameWrite(0, 0)
                theLineFrameWriteList.add(frameWrite)
            }
            frameWriteMap[line] = theLineFrameWriteList
        }

        runnable = object : BukkitRunnable() {
            override fun run() {

                if (viewers.isEmpty()) {
                    cancel()
                    return
                }

                if (dialogFile.ownTextContent!!.size == isCancelsMap.size) {
                    cancel()
                    return
                }

                // 一个全息所有行内容
                val holoTextList = mutableListOf<String>()

                for (line in 0 until dialogFile.ownTextContent!!.size) {
                    val theLineTagTextList = dialogFile.getOwnTheLineList(line)
                    holoTextList.add(UtilAnimation().theLineTextAnimation(line, frame,
                        theLineTagTextList, frameWriteMap, isCancelsMap/*, textContentMap*/))
                }

                holo.textList = holoTextList
                holo.updateContent()

                frame++
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 1L)
    }
}