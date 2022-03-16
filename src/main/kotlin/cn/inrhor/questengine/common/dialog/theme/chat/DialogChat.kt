package cn.inrhor.questengine.common.dialog.theme.chat

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.DialogManager.refresh
import cn.inrhor.questengine.common.dialog.DialogManager.setId
import org.bukkit.entity.Player
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
    val viewers: MutableSet<Player>,
    var scrollIndex: Int = 0,
    var json: TellrawJson = TellrawJson()
): DialogTheme(type = Type.Chat) {

    val replyChat: ReplyChat = ReplyChat(this, dialogModule.reply)

    override fun play() {
        viewers.forEach {
            textViewer(it)
            val pData = DataStorage.getPlayerData(it)
            pData.dialogData.addDialog(dialogModule.dialogID, this)
            pData.chatCache.open()
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
            val pData = DataStorage.getPlayerData(it)
            pData.chatCache.close(it)
            pData.dialogData.dialogMap.remove(dialogModule.dialogID)
        }
    }

    override fun addViewer(viewer: Player) {
        viewers.add(viewer)
    }

    override fun deleteViewer(viewer: Player) {
        viewers.remove(viewer)
    }

}