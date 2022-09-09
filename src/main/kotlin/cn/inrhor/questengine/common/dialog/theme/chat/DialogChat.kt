package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.dialog.DialogManager.setId
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
    var playing: Boolean = false,
    var json: TellrawJson = TellrawJson()
): DialogTheme(type = Type.Chat) {

    val replyChat: ReplyChat = ReplyChat(this, dialogModule.reply)

    override fun play() {
        playing = true
        viewers.forEach {
            textViewer(it)
            val pData = it.getPlayerData()
            pData.dialogData.addDialog(dialogModule.dialogID, this)
            pData.chatCache.open()
            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Int.MAX_VALUE, 1))
            it.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
        }
        DialogManager.spaceDialog(dialogModule, this)
    }

    fun textViewer(viewer: Player) {
        val content = dialogModule.dialog
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
        parserContent(viewer, list)
    }

    fun parserContent(viewer: Player, list: MutableList<MutableList<DataText>>, frameLine: Int = 0) {
        if (!viewer.isOnline) return
        submit(delay = 3L) {
            if (finishParser(list)) {
                playing = false
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
            pData.chatCache.close(it)
            pData.dialogData.dialogMap.remove(dialogModule.dialogID)
            it.removePotionEffect(PotionEffectType.BLINDNESS)
            it.removePotionEffect(PotionEffectType.INVISIBILITY)
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