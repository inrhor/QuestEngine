package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.DialogType
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.api.event.DialogEvent
import cn.inrhor.questengine.api.manager.TemplateManager.getTemplate
import cn.inrhor.questengine.api.packet.entityRotation
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.PlayerData
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.dialog.DialogManager.end
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import cn.inrhor.questengine.common.dialog.FlagDialog
import cn.inrhor.questengine.common.dialog.hasFlag
import cn.inrhor.questengine.common.nms.NMS
import cn.inrhor.questengine.utlis.variableReader
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common5.util.printed
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.compat.replacePlaceholder

/**
 * 聊天框对话
 */
class DialogChat(
    override val dialogModule: DialogModule,
    override val viewers: MutableSet<Player>,
    override val npcLoc: Location,
    var scrollIndex: Int = 0,
    var playing: Boolean = false, // 播放状态，防止滚动、跳过
    var json: TellrawJson = TellrawJson()
): DialogTheme(type = DialogType.CHAT) {

    /**
     * 发送已经解析的json对话内容
     */
    fun sendFullDialog(viewer: Player) {
        json.setId().sendTo(adaptPlayer(viewer))
    }

    val replyChat: ReplyChat = ReplyChat(this, dialogModule.reply)

    /**
     * 执行对话flag
     */
    fun executeFlag(viewer: Player, pData: PlayerData, play: Boolean = true) {
        val flag = dialogModule.flag
        if (!flag.hasFlag(FlagDialog.NO_SCREEN)) {
            if (play) {
                pData.chatCache.open()
            }else {
                pData.chatCache.close(viewer, flag.hasFlag(FlagDialog.NO_CACHE_CHAT))
            }
        }
        if (!flag.hasFlag(FlagDialog.NO_CLEAR)) {
            if (!play) TellrawJson().refresh().sendTo(adaptPlayer(viewer))
        }
        submit { // 同步，因为播放对话是异步的
            if (flag.hasFlag(FlagDialog.SLOW)) {
                if (play) {
                    viewer.addPotionEffect(PotionEffect(PotionEffectType.SLOW, Int.MAX_VALUE, 1))
                }else {
                    viewer.removePotionEffect(PotionEffectType.SLOW)
                }
            }
            if (flag.hasFlag(FlagDialog.BLINDNESS)) {
                if (play) {
                    viewer.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Int
                        .MAX_VALUE, 1))
                }else {
                    viewer.removePotionEffect(PotionEffectType.BLINDNESS)
                }
            }
        }
        if (flag.hasFlag(FlagDialog.KEEP_VIEW)) {
            val loc = viewer.location.clone()
            submit {
                viewer.walkSpeed = 0.0F
            }
            submit(async = true, period = 20L) {
                if (!viewers.contains(viewer)) {
                    cancel()
                    return@submit
                }
                entityRotation(viewers, viewer.entityId, loc.yaw)
            }
        }
    }

    /**
     * 异步播放对话
     */
    override fun play() {
        // 播放状态，防止滚动、跳过
        playing = true
        // 对所有可视者处理
        viewers.forEach {
            // 事件
            DialogEvent(it, dialogModule).call()
            val pData = it.getPlayerData()
            val dData = pData.dialogData
            // 终止此前的聊天对话
            dData.end(type)
            // 添加对话到可视者数据
            dData.addDialog(dialogModule.dialogID, this)
            // 执行对话flag
            executeFlag(it, pData)
            // 对话空间检查
            DialogManager.spaceDialog(dialogModule, this)
            // 对话文本发送
            textViewer(it)
        }
    }

    /**
     * 对话文本处理
     */
    fun textViewer(viewer: Player) {
        var content = dialogModule.dialog
        // 对话模板
        val temp = dialogModule.template
        if (temp.isNotEmpty()) {
            val templateFrame = temp.getTemplate()
            if (templateFrame != null) {
                content = templateFrame.replyList(content)
            }
        }
        val list = mutableListOf<MutableList<DataText>>()
        for (element in content) {
            val c = element.replacePlaceholder(viewer).colored()
            val line = mutableListOf<DataText>()
            c.variableReader().forEach { v ->
                val o = v.variableReader("[[", "]]")
                val t = DisplayType.valueOf(o[0].uppercase())
                val s = o[1]
                line.add(DataText(t, s))
            }
            list.add(line)
        }
        // 开始解析文本并发送
        parserContent(viewer, list)
    }

    /**
     * 解析文本
     */
    fun parserContent(viewer: Player, list: MutableList<MutableList<DataText>>, frameLine: Int = 0) {
        if (!viewer.isOnline) return
        submit(delay = dialogModule.speed.toLong()) {
            if (finishParser(list)) {
                // 对话内容播放结束
                playing = false
                // 发送回复选择
                replyChat.play()
                return@submit
            }
            val tellrawJson = TellrawJson()
            tellrawJson.refresh() // 清除聊天框
            tellrawJson.append("")
            var newLine = 0
            var hasAnimation = false
            for (l in 0 until list.size) { // 每行
                val theLine = list[l]
                theLine.forEach { tag -> //每独立标签
                    if (tag.type == DisplayType.ANIMATION) {
                        if (l < frameLine) {
                            tellrawJson.append(tag.textFrame())
                        }else if (l == frameLine) {
                            tellrawJson.append(tag.textFrame())
                        }
                        if (tag.finish) newLine++
                        hasAnimation = true
                    }else {
                        tellrawJson.append(tag.s)
                        tag.finish = true
                    }
                }
                tellrawJson.newLine()
                if (!hasAnimation) {
                    newLine++
                }
            }
            tellrawJson.setId().sendTo(adaptPlayer(viewer))
            json = tellrawJson
            parserContent(viewer, list, newLine)
        }
    }

    fun finishParser(list: MutableList<MutableList<DataText>>): Boolean {
        list.forEach {
            it.forEach { d ->
                if (!d.finish) return false
            }
        }
        return true
    }

    override fun end() {
        viewers.forEach {
            val pData = it.getPlayerData()
            pData.dialogData.dialogMap.remove(dialogModule.dialogID)
            executeFlag(it, pData, false)
        }
        viewers.clear()
    }

    override fun addViewer(viewer: Player) {
        viewers.add(viewer)
    }

    override fun deleteViewer(viewer: Player) {
        viewers.remove(viewer)
    }

    class DataText(val type: DisplayType, val s: String, var context: List<String> = listOf(),
                   var finish: Boolean = false, var frame: Int = 0) {

        fun textFrame(): String {
            return if (frame <= context.size-1) {
                frame++
                context[frame-1]
            }else {
                finish = true
                context.last()
            }
        }

        init {
            if (type == DisplayType.ANIMATION) context = s.printed()
        }

    }

    enum class DisplayType {
        STATIC, ANIMATION
    }

}