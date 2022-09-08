package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import cn.inrhor.questengine.utlis.variableReader
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.util.addSafely
import taboolib.common.util.setSafely
import taboolib.common5.Demand
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
    var json: TellrawJson = TellrawJson()
): DialogTheme(type = Type.Chat) {

    val replyChat: ReplyChat = ReplyChat(this, dialogModule.reply)

    override fun play() {
        viewers.forEach {
            textViewer(it)
            val pData = it.getPlayerData()
            pData.dialogData.addDialog(dialogModule.dialogID, this)
            pData.chatCache.open()
            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, Int.MAX_VALUE, 1))
            it.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
        }
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

    fun parserContent(viewer: Player, list: MutableList<MutableList<DataText>>, line: Int = 0) {
        if (!viewer.isOnline) return
        submit(delay = 3L) {
            val tellrawJson = TellrawJson()
            tellrawJson.refresh() // 清除聊天框
            tellrawJson.append("")
            var newLine = line
            for (l in 0 until list.size) { // 每行
                val theLine = list[l]
                theLine.forEach { tag -> //每独立标签
                    if (tag.type == DisplayType.ANIMATION) {
                        if (l < line) {
                            tellrawJson.append(tag.textFrame())
                        }else if (l == line) {
                            tellrawJson.append(tag.textFrame())
                            if (tag.finish) newLine++
                        }
                    }else {
                        tellrawJson.append(tag.s)
                    }
                }
                tellrawJson.newLine()
            }
            tellrawJson.setId().sendTo(adaptPlayer(viewer))
            val l = staticLine+newLine
            info("n $l  s "+list.size)
            if (l >= list.size) {
                replyChat.play()
                return@submit
            }
            parserContent(viewer, list, newLine)
        }
    }

    /**
     * @param index 当前行打印的序号
     */
    /*fun parserContent(viewer: Player, list: MutableList<MutableList<DataText>>, line: Int = 0, index: Int = 0) {
        if (!viewer.isOnline) return
        submit(delay = 3L) {
            val tellrawJson = TellrawJson()
            tellrawJson.refresh()
            val size = list.size
            info("size $size line $line")
            if (size <= line) {
                replyChat.play()
                return@submit
            }
            var newLine = line
            for (e in 0 until list.size) { // 行
                val data = list[e]
                for (d in 0 until data.size) {
                    val it = data[d]
                    if (it.type == DisplayType.ANIMATION) {
                        if (line == e) {
                            tellrawJson.append(it.context[index])
                        }else if (line > e) {
                            tellrawJson.append(it.context.last())
                        }
                    }else {
                        tellrawJson.append(it.s)
                    }
                    info("??? "+it.s)
//                    info("标签 $d  限量标签 "+(data.size-1)+"  终行 $line 当前行 $e")
                    info("line $line  e $e   "+(line <= e))
                    info("d $d  size "+(data.size-1))
                    if (d >= data.size-1 && line <= e) {
                        if (it.type == DisplayType.ANIMATION) {
                            info("si "+(it.context.size-1)+"   index $index")
                            if (it.context.size-1 <= index) {
                                info("newLine")
                                newLine++
                            }
                        }else {
                            info("newLine++++")
                            newLine++
                        }
                    }
                }
                tellrawJson.newLine()
            }
            json = tellrawJson
            tellrawJson.setId().sendTo(adaptPlayer(viewer))
            val ex = if (newLine > line) 0 else index+1
            parserContent(viewer, list, newLine, ex)
        }
    }*/

    override fun end() {
        viewers.forEach {
            val pData = it.getPlayerData()
            pData.chatCache.close(it)
            pData.dialogData.dialogMap.remove(dialogModule.dialogID)
            submit {
                it.removePotionEffect(PotionEffectType.BLINDNESS)
                it.removePotionEffect(PotionEffectType.INVISIBILITY)
            }
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