package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.theme.chat.DialogChat
import cn.inrhor.questengine.common.dialog.theme.hologram.core.DialogHologram
import cn.inrhor.questengine.script.kether.runEval
import cn.inrhor.questengine.script.kether.runEvalSet

import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.chat.TellrawJson
import taboolib.module.lang.sendLang
import taboolib.platform.util.asLangText


object DialogManager {
    /**
     * 成功注册的对话模块
     */
    private val dialogMap = mutableMapOf<String, DialogModule>()

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        if (exist(dialogID)) {
            console().sendLang("DIALOG-EXIST_DIALOG_ID", UtilString.pluginTag, dialogID)
            return
        }
        dialogMap[dialogID] = dialogModule
    }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogMap.remove(dialogID)}

    /**
     * 对话ID 是否存在
     */
    fun exist(dialogID: String) = dialogMap.contains(dialogID)

    /**
     * 获取对话对象
     */
    fun get(dialogID: String) = dialogMap[dialogID]

    fun getMap() = dialogMap

    /**
     * 清空对话对象Map
     */
    fun clearMap() = dialogMap.clear()

    /**
     * 获取玩家是否进行相同对话ID的对话
     */
    fun hasDialog(players: MutableSet<Player>, dialogID: String): Boolean {
        players.forEach {
            if (hasDialog(it, dialogID)) return true
        }
        return false
    }

    fun hasDialog(player: Player, dialogID: String): Boolean {
        player.getPlayerData().dialogData.dialogMap[dialogID]?: return false
        return true
    }

    fun TellrawJson.refresh(): TellrawJson {
        for (i in 0..32) this.newLine()
        return this
    }

    fun TellrawJson.setId(): TellrawJson {
        return this.insertion("@d31877bc-b8bc-4355-a4e5-9b055a494e9f")
    }

    /**
     * 根据NPCID并判断玩家集合是否满足条件，返回dialogModule
     */
    fun returnNpcDialog(players: MutableSet<Player>, npcID: String): DialogModule? {
        dialogMap.values.forEach {
            if (!it.npcIDs.contains(npcID)) return@forEach
            if (runEvalSet(players, it.condition)) {
                val dialogID = it.dialogID
                if (!hasDialog(players, dialogID)) return it
            }
        }
        return null
    }

    fun sendDialog(players: MutableSet<Player>, npcLoc: Location, npcID: String) {
        val dialogModule = returnNpcDialog(players, npcID) ?: return
        if (dialogModule.type == "holo") {
            sendDialogHolo(players, dialogModule, npcLoc)
        }else sendDialogChat(players, dialogModule, npcLoc)
    }

    fun sendDialog(player: Player, dialogID: String, loc: Location = player.location) {
        if (hasDialog(player, dialogID)) return
        val dialogModule = get(dialogID)?: return
        if (dialogModule.type == "holo") {
            sendDialogHolo(player, dialogModule, loc)
        }else sendDialogChat(mutableSetOf(player), dialogModule, loc)
    }

    private fun sendDialogChat(players: MutableSet<Player>, dialogModule: DialogModule, npcLoc: Location) {
        DialogChat(dialogModule, players, npcLoc).play()
    }

    private fun sendDialogHolo(players: MutableSet<Player>, dialogModule: DialogModule, npcLoc: Location) {
        val dialog = DialogHologram(dialogModule, npcLoc, players)
        dialog.play()
        spaceDialog(dialogModule, dialog)
    }

    private fun sendDialogHolo(player: Player, dialogModule: DialogModule, location: Location = player.location) {
        val holoDialog = DialogHologram(dialogModule, location, mutableSetOf(player))
        holoDialog.play()
        spaceDialog(dialogModule, holoDialog)
    }

    fun sendBarHelp(dialogChat: DialogChat) {
        submit(async = true, period = 20L) {
            if (dialogChat.viewers.isEmpty()) {
                cancel()
                return@submit
            }
            dialogChat.viewers.forEach {
                adaptPlayer(it).sendActionBar(it.asLangText("DIALOG-CHAT-HELP"))
            }
        }
    }

    fun spaceDialog(dialogModule: DialogModule, dialogTheme: DialogTheme) {
        val space = dialogModule.space
        if (!space.enable) return
        val id = dialogModule.dialogID
        submit(period = 5L) {
            val viewers = dialogTheme.viewers
            if (viewers.isEmpty()) {
                cancel(); return@submit
            }
            if (!checkSpace(viewers, space.condition, dialogTheme.npcLoc)) {
                endHoloDialog(viewers.first(), id)
                cancel()
                return@submit
            }
        }
    }

    fun checkSpace(players: MutableSet<Player>, condition: String, loc: Location): Boolean {
        players.forEach {
            if (!runEval(it, condition.replace("{{location}}", " where location "+loc.world?.name+
                        " "+loc.x+" "+loc.y+" "+loc.z))) return false
        }
        return true
    }

    fun endHoloDialog(player: Player, dialogID: String) {
        val pDate = player.getPlayerData()
        pDate.dialogData.endHoloDialog(dialogID)
    }
}