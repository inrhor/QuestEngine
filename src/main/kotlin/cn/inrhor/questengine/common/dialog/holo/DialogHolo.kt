package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.Dialog
import cn.inrhor.questengine.common.dialog.animation.FrameWrite
import cn.inrhor.questengine.common.dialog.animation.TagText
import cn.inrhor.questengine.common.hologram.IHolo
import cn.inrhor.questengine.utlis.public.MsgUtil
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
//        var timeLong = 0
        val dialogFile = Dialog().getDialog(holo.holoID)!!

        val frameWriteMap = mutableMapOf<Int, MutableList<FrameWrite>>()
//        val textContentMap = mutableMapOf<Int, MutableList<String>>()
        val isCancelsMap = mutableMapOf<Int, MutableList<Boolean>>()

        for (line in 0 until dialogFile.ownTextContent!!.size) {
            val theLineFrameWriteList = mutableListOf<FrameWrite>()
            repeat(dialogFile.getOwnTheLineList(line).size) {
                val frameWrite = FrameWrite(0, 0)
                theLineFrameWriteList.add(frameWrite)
            }
            frameWriteMap[line] = theLineFrameWriteList

//            val textContentList = mutableListOf<String>()
//            textContentMap[line] = textContentList
        }

        runnable = object : BukkitRunnable() {
            override fun run() {

                /*dialogFile.ownTextContent!!.forEach {
                    theLineTextAnimation(frame, )
                }*/

                if (dialogFile.ownTextContent!!.size == isCancelsMap.size) {
                    cancel()
                    return
                }


                // 一个全息所有行内容
                val holoTextList = mutableListOf<String>()

                for (line in 0 until dialogFile.ownTextContent!!.size) {
                    val theLineTagTextList = dialogFile.getOwnTheLineList(line)
//                    val theLineTimeLong = theLineAllTimeLong(theLineTagTextList)
//                    if (timeLong < theLineTimeLong) {
//                        timeLong = theLineTimeLong
//                    }
                    holoTextList.add(theLineTextAnimation(line, frame,
                        theLineTagTextList, frameWriteMap, isCancelsMap/*, textContentMap*/))
                }

                holo.textList = holoTextList
                holo.updateContent()
                /*holo.textList.forEach {
                    MsgUtil.send("list  $it")
                }*/

                frame++
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 3L)

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
    /*fun theLineAllTimeLong(tagTextList: MutableList<TagText>): Int {
        var timeLong = 0
        var delay = 0
        tagTextList.forEach {
            if (it.timeLong >= timeLong) {
                timeLong += it.timeLong
                MsgUtil.send("timeLong $timeLong")
            }
            if (it.delay > delay) {
                delay = it.delay
            }
        }
        return timeLong+delay
    }*/

    // 这一行动态标签的动画实现
    fun theLineTextAnimation(
        line: Int,
        frame: Int,
        tagTextList: MutableList<TagText>,
        frameWriteMap: MutableMap<Int, MutableList<FrameWrite>>,
        isCancelsMap: MutableMap<Int, MutableList<Boolean>>/*,
        textContentMap: MutableMap<Int, MutableList<String>>*/): String {

        // test
        MsgUtil.send("   frame $frame")
        /*if (timeLong < frame) {
            runnable!!.cancel()
            return ""
        }*/

        val contentList = mutableListOf<String>()
        val isCancels = mutableListOf<Boolean>()

        for (index in 0 until tagTextList.size) {
            val tagText = tagTextList[index]
            val frameWrite = frameWriteMap[line]!![index]
            val textFrame = frameWrite.textFrame
            val writeSpeed = frameWrite.writeSpeed
            val size = tagText.contentList.size

            if (textFrame >= size) {
                // test
//                MsgUtil.send("textFrame $textFrame   size $size")
                contentList.add(tagText.contentList[size-1])
                // test
                isCancels.add(false)
                MsgUtil.send("cont")
                continue
            }

            if (frame >= tagText.delay) {
//                MsgUtil.send("writeSpeed $writeSpeed   speed "+tagText.speed)
                if (writeSpeed >= tagText.speed) {

                    contentList.add(index, tagText.contentList[textFrame])
//                    MsgUtil.send("add $index  "+tagText.contentList[textFrame])

                    frameWrite.textFrame++
                    frameWrite.writeSpeed = 0

                }else {
                    contentList.add(tagText.contentList[textFrame])
                    frameWrite.writeSpeed++
                }
            }
        }
        if (isCancels.isNotEmpty() and (isCancels.size == tagTextList.size)) {
            isCancelsMap[line] = isCancels
        }

        return contentList.toString()
    }
}