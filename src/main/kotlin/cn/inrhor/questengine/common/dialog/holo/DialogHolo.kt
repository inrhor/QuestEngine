package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.Dialog
import cn.inrhor.questengine.common.dialog.animation.TagText
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

        runnable = object : BukkitRunnable() {
            // 主动态帧
            var frame = 0
            val dialogFile = Dialog().getDialog(holo.holoID)!!

            override fun run() {

                // 一个全息所有行内容
                val holoTextList = mutableListOf<String>()
                repeat(holo.textList.size) {
                    holoTextList.add("")
                }



                frame++
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 1L)

        // lj，重写
        /*var frame = 0
        var textFrame = 0
        var writeSpeed = 0
        val dialogFile = Dialog().getDialog(holo.holoID)!!
        val textList = dialogFile.getOwnTheLineList(0)*/
        /*runnable = object : BukkitRunnable() {
            override fun run() {
//                MsgUtil.send("frame $frame   textFrame $textFrame")

                // 一个全息所有行内容
                val holoTextList = mutableListOf<String>()
                repeat(holo.textList.size) {
                    holoTextList.add("")
                }

                var timeLong = 0

                textList.forEach {

                    MsgUtil.send("timeLong "+it.timeLong)
                    timeLong += it.timeLong

                    if (timeLong < frame) {
                        cancel()
                        return
                    }

                    if (textFrame >= it.contentList.size) {
                        MsgUtil.send("stop")
                        frame = 0
                        textFrame = 0
                        writeSpeed = 0
                        return
                    }

                    if (frame >= it.delay) {
                        if (writeSpeed >= it.speed) {

                            // 拼接算法待写 根据tagIndex排序
                            *//*holoTextList[0] += it.contentList[textFrame]
                            holo.textList[0] = holoTextList[0]*//*
                            it.textFrame = textFrame

                            writeSpeed = 0
                            textFrame++
                        }else writeSpeed++
                    }
                }
                holo.textList[0] = Util().getAnimationText(textList, frame)
                frame++
                holo.updateContent(0)
            }
        };(runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 1L)*/
    }

    // 这一行动态总时长
    fun theLineAllTimeLong(tagTextList: MutableList<TagText>): Int {
        var timeLong = 0
        tagTextList.forEach {
            timeLong += it.timeLong
        }
        return timeLong
    }

    // 这一行动态标签的动画实现
    fun theLineTextAnimation(frame: Int, tagTextList: MutableList<TagText>) {

    }
}