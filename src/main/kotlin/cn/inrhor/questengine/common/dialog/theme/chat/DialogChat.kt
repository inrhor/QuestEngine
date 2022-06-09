package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.dialog.DialogManager.setId
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
        val list = mutableListOf<List<String>>()
        content.forEach {
            list.add(it.replacePlaceholder(viewer).colored().printed())
        }
        parserContent(viewer, list)
    }

    fun parserContent(viewer: Player, list: MutableList<List<String>>, index: Int = 0) {
        val size = list.size
        if (list.isEmpty()) TellrawJson().refresh()
        json = TellrawJson().refresh()
        if (index>0) {
            for (i in 0 until index) {
                json.append(list[i].last()).newLine()
            }
        }
        if (size-1 == 0 || size-1 < index) {
            replyChat.play()
            return
        }
        textSend(viewer, list, index, list[index], 0)
    }

    private fun textSend(viewer: Player, element: MutableList<List<String>>, index: Int, list: List<String>, line: Int) {
        submit(async = true, delay = 3L) {
            val new = TellrawJson()
            new.append(json).append(list[line]).newLine().setId().sendTo(adaptPlayer(viewer))
            if (line != list.size-1) {
                textSend(viewer, element, index, list, line+1)
            }else {
                parserContent(viewer, element, index+1)
            }
        }
    }

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

}