package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.dialog.animation.text.FrameWrite
import cn.inrhor.questengine.common.dialog.animation.UtilAnimation
import cn.inrhor.questengine.common.hologram.IHolo
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class DialogHolo(
    var holo: IHolo,
    var viewers: MutableSet<Player>,
    var dialogCube: DialogCube,
    var runnable: BukkitRunnable?
) {

    constructor(holo: IHolo, viewers: MutableSet<Player>, dialogCube: DialogCube)
            : this(holo, viewers, dialogCube,null)

    fun runRunnable() {

        // 主动态帧
        var frame = 0

        val frameWriteMap = mutableMapOf<Int, MutableList<FrameWrite>>()
        val isCancelsTextMap = mutableMapOf<Int, MutableList<Boolean>>()
        val isCancelsItemMap = mutableMapOf<Int, Boolean>()

        val ownTextInitContent = dialogCube.ownTextInitContent

        for (line in 0 until ownTextInitContent.size) {
            val theLineFrameWriteList = mutableListOf<FrameWrite>()
            repeat(dialogCube.getTheLineList(line).size) {
                val frameWrite = FrameWrite(0, 0)
                theLineFrameWriteList.add(frameWrite)
            }
            frameWriteMap[line] = theLineFrameWriteList
        }

        val ownTextSize = ownTextInitContent.size
        val ownItemSize = dialogCube.ownItemInitContent.getDialogItemList().size

        runnable = object : BukkitRunnable() {
            override fun run() {

                if (viewers.isEmpty()) {
                    cancel()
                    return
                }

                if (!holo.hasSendReply) {
                    val dialogFrame = dialogCube.frame
                    if ((dialogFrame==-1) or (dialogFrame==frame)) {
                        dialogCube.replyCubeList.forEach{
                            holo.sendReplyHolo()
                        }
                    }
                }

                if ((ownTextSize == isCancelsTextMap.size) and
                    (ownItemSize == isCancelsItemMap.size)) {
                    cancel()
                    return
                }

                // 一个全息所有行文字内容
                val holoTextList = mutableListOf<String>()

                // Text
                for (line in 0 until ownTextSize) {
                    val theLineTagTextList = dialogCube.getTheLineList(line)
                    holoTextList.add(UtilAnimation().theLineTextAnimation(line, frame,
                        theLineTagTextList, frameWriteMap, isCancelsTextMap))
                }
                holo.textList = holoTextList

                // Item
                for (line in 0 until ownItemSize) {
                    val dialogItem = dialogCube.getTheLineItem(line)
                    if (dialogItem.delay == frame) {
                        holo.spawnItem(line)
                        isCancelsItemMap[line] = false
                    }
                }

                holo.updateContent()

                frame++
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 1L)
    }
}
