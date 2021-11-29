package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextParser
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.script.kether.evalBoolean
import cn.inrhor.questengine.script.kether.evalBooleanSet
import cn.inrhor.questengine.utlis.file.FileUtil
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.lang.sendLang
import taboolib.platform.compat.replacePlaceholder
import java.util.*


object DialogManager {
    /**
     * 成功注册的对话
     */
    var dialogMap = mutableMapOf<String, DialogModule>()

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        if (exist(dialogID)) {
            console().sendLang("DIALOG-EXIST_DIALOG_ID", UtilString.pluginTag, dialogID)
            return
        }
        val itemContents = mutableListOf<String>()
        for (script in dialogModule.dialog) {
            val iUc = script.uppercase()
            when {
                iUc.startsWith("ITEMWRITE") -> {
                    itemContents.add(script)
                }
            }
        }
        val itemParser = ItemParser(itemContents)
        itemParser.init(dialogID)

        dialogModule.playItem = itemParser.dialogItemList

        dialogMap[dialogID] = dialogModule
    }

    fun animation(dialogID: String, player: Player) {
        val dialogModule = dialogMap[dialogID]?: return
        val textContents = mutableListOf<String>()
        for (script in dialogModule.dialog) {
            val iUc = script.uppercase()
            when {
                iUc.startsWith("TEXT") -> {
                    textContents.add(script.replacePlaceholder(player))
                }
            }
        }
        val textParser = TextParser(textContents)
        textParser.init(dialogID, "dialog")

        dialogModule.playText = textParser.dialogTextList
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
        val holoDialog = DataStorage.getPlayerData(player).dialogData.holoDialogMap[dialogID]?: return false
        holoDialog.forEach {
            if (it.dialogModule.dialogID == dialogID) return true
        }
        return false
    }

    /**
     * 根据NPCID并判断玩家集合是否满足条件，返回dialogModule
     */
    fun returnCanDialogHolo(players: MutableSet<Player>, npcID: String): DialogModule? {
        dialogMap.values.forEach {
            if (!it.npcIDs.contains(npcID)) return@forEach
            if (evalBooleanSet(players, it.condition)) {
                val dialogID = it.dialogID
                if (!hasDialog(players, dialogID)) return it
            }
        }
        return null
    }

    fun sendDialogHolo(players: MutableSet<Player>, npcID: String, npcLoc: Location) {
        val dialogModule = returnCanDialogHolo(players, npcID)?: return
        val holoDialog = HoloDialog(dialogModule, npcLoc, players)
        holoDialog.run()
        spaceDialogHolo(dialogModule, holoDialog)
    }

    fun sendDialogHolo(player: Player, dialogID: String, location: Location = player.location) {
        if (hasDialog(player, dialogID)) return
        if (exist(dialogID)) {
            val dialogModule = get(dialogID)?: return
            val holoDialog = HoloDialog(dialogModule, location, mutableSetOf(player))
            holoDialog.run()
            spaceDialogHolo(dialogModule, holoDialog)
        }
    }

    fun spaceDialogHolo(dialogModule: DialogModule, holoDialog: HoloDialog) {
        val space = dialogModule.space
        if (!space.enable) return
        val id = dialogModule.dialogID
        submit(async = true, period = 5L) {
            val viewers = holoDialog.viewers
            if (viewers.isEmpty()) {
                cancel(); return@submit
            }
            if (!checkSpace(viewers, space.condition, holoDialog.npcLoc)) {
                endHoloDialog(viewers.first(), id)
                cancel()
                return@submit
            }
        }
    }

    fun checkSpace(players: MutableSet<Player>, condition: List<String>, loc: Location): Boolean {
        players.forEach {
            condition.forEach { cd ->
                val shell = if (cd.lowercase().startsWith("spacerange"))  cd+
                        " where location *"+loc.world?.name+
                        " *"+loc.x+" *"+loc.y+" *"+loc.z else cd
                if (!evalBoolean(it, shell)) return false
            }
        }
        return true
    }

    fun endHoloDialog(player: Player, dialogID: String) {
        val pDate = DataStorage.getPlayerData(player)
        pDate.dialogData.endHoloDialog(dialogID)
    }
}