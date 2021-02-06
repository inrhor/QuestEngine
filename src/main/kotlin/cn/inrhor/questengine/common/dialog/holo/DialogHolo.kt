package cn.inrhor.questengine.common.dialog.holo

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.dialog.Dialog
import cn.inrhor.questengine.common.hologram.IHolo
import cn.inrhor.questengine.utlis.public.MsgUtil
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class DialogHolo(
    var holo: IHolo,
    var viewers: MutableSet<Player>,
    var runnable: BukkitRunnable?
) {

    constructor(holo: IHolo, viewers: MutableSet<Player>) : this(holo, viewers, null)


    @TSchedule
    fun runRunnable() {
        var frame = 0
        var textFrame = 0
        var writeSpeed = 0
        val dialogFile = Dialog().getDialog(holo.holoID)!!
        val textList = dialogFile.getOwnTheLineList(0)
        runnable = object : BukkitRunnable() {
            override fun run() {
//                MsgUtil.send("frame $frame   textFrame $textFrame")

                // 一个全息所有行内容
                val holoTextList = mutableListOf<String>()
                repeat(holo.textList.size) {
                    holoTextList.add("")
                }

                textList.forEach {
                    if (textFrame >= it.contentList.size) {
                        MsgUtil.send("cancel")
                        frame = 0
                        textFrame = 0
                        writeSpeed = 0
                        cancel()
                        return
                    }
//                    allTimeLong = it.timeLong
//                    MsgUtil.send("textFrame $textFrame  size "+it.contentList.size+"frame $frame")

                    if (frame >= it.delay) {
//                        MsgUtil.send("eeeFrame  $textFrame  t   speed $writeSpeed")
                        if (writeSpeed >= it.speed) {
//                            holo.textList[0] = it.contentList[textFrame]
//                            holo.updateContent(0)
                            MsgUtil.send("??  "+holoTextList[0])
                            holoTextList[0] += it.contentList[textFrame]
                            MsgUtil.send("asdasd??  "+holoTextList[0])
                            holo.textList[0] = holoTextList[0]
                            writeSpeed = 0
                            textFrame++
                        }else writeSpeed++
                    }
                }
                frame++

                holo.updateContent(0)
            }
        };(runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0, 1L)

        /*runnable = object : BukkitRunnable() {
            override fun run() {
                var holoText = ""
                if (viewers.isEmpty()) {
                    cancel()
                    return
                }
                val dialogFile = Dialog().getDialog(holo.holoID)!!
                val textList = dialogFile.getOwnTheLineList(0)
                textList.forEach {
                    if (textFrame >= it.contentList.size) {
                        MsgUtil.send("cancel")
                        cancel()
                        return
                    }
//                    allTimeLong = it.timeLong
                    MsgUtil.send("textFrame $textFrame   size "+it.contentList.size+"   frame $frame")
                    if (frame >= it.delay) {
//                        MsgUtil.send("eeeFrame  $textFrame  t   speed $writeSpeed")
                        if (writeSpeed >= it.speed) {
                            *//*holo.textList[0] = it.contentList[textFrame]
                            holo.updateContent(0)*//*
                            holoText += it.contentList[textFrame]
                            writeSpeed = 0
                            textFrame++
                        }else writeSpeed++
                    }
                }
                frame++
                holo.textList[0] = holoText
                holo.updateContent(0)
            }
        }
        (runnable as BukkitRunnable).runTaskTimer(QuestEngine.plugin, 0L, 1L)*/
    }
}