package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextParser
import cn.inrhor.questengine.common.dialog.optional.holo.core.HoloDialog
import cn.inrhor.questengine.script.kether.KetherHandler
import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.UtilString
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.platform.console
import taboolib.module.lang.sendLang
import java.util.*


object DialogManager {
    /**
     * 成功注册的对话
     */
    private var dialogMap: HashMap<String, DialogModule> = LinkedHashMap()

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        if (exist(dialogID)) {
            console().sendLang("DIALOG-EXIST_DIALOG_ID", UtilString.pluginTag, dialogID)
            return
        }
        val itemContents = mutableListOf<String>()
        val textContents = mutableListOf<String>()
        for (script in dialogModule.dialog) {
            val iUc = script.uppercase(Locale.getDefault())
            when {
                iUc.startsWith("TEXT") -> {
                    textContents.add(script)
                }
                iUc.startsWith("ITEMNORMAL") -> {
                    itemContents.add(script)
                }
            }
        }
        val itemParser = ItemParser(itemContents)
        val textParser = TextParser(textContents)
        itemParser.init(dialogID)
        textParser.init(dialogID, "dialog")

        dialogModule.playItem = itemParser.dialogItemList
        dialogModule.playText = textParser.dialogTextList

        dialogMap[dialogID] = dialogModule
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        val dialogFolder = GetFile.getFile("space/dialog/example.yml", "DIALOG-NO_FILES", true)
        GetFile.getFileList(dialogFolder).forEach{
            DialogFile.checkRegDialog(it)
        }
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
            if (KetherHandler.evalBooleanSet(players, it.condition)) {
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
    }
}