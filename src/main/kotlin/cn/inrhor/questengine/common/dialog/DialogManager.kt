package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.common.dialog.optional.holo.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.optional.holo.animation.parser.TextParser
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.file.GetFile
import cn.inrhor.questengine.utlis.location.LocationTool
import cn.inrhor.questengine.utlis.public.MsgUtil
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*


class DialogManager {
    companion object {
        /**
         * 成功注册的对话
         */
        private var dialogFileMap: HashMap<String, DialogModule> = LinkedHashMap()
    }

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogModule: DialogModule) {
        if (exist(dialogID)) {
            TLocale.sendToConsole("DIALOG.EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
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
        itemParser.init()
        textParser.init("dialog")

        // 我写晕了，重新思考整理下

        dialogFileMap[dialogID] = dialogModule
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        val dialogFolder = GetFile().getFile("space/dialog", "DIALOG.NO_FILES")
        GetFile().getFileList(dialogFolder).forEach{
            DialogFile().checkRegDialog(it)
        }
    }

    /**
     * 删除对话
     */
    fun remove(dialogID: String) {dialogFileMap.remove(dialogID)}

    /**
     * 对话ID 是否存在
     */
    fun exist(dialogID: String) = dialogFileMap.contains(dialogID)

    /**
     * 获取对话对象
     */
    fun get(dialogID: String) = dialogFileMap[dialogID]

    /**
     * 清空对话对象Map
     */
    fun clearMap() = dialogFileMap.clear()

    /**
     * 根据NPCID并判断玩家集合是否满足条件，返回dialogModule
     */
    fun returnDialogHolo(players: MutableSet<Player>, npcID: String): DialogModule? {
        for ((_, dialogModule) in dialogFileMap) {
            if (dialogModule.npcID != npcID) continue
            if (KetherHandler.evalBooleanSet(players, dialogModule.condition)) return dialogModule
        }
        return null
    }

    fun sendDialogHolo(players: MutableSet<Player>, npcID: String, npcLoc: Location) {
        MsgUtil.send("test  "+returnDialogHolo(players, npcID))
        val dialogModule = returnDialogHolo(players, npcID)?: return
        /*val holo = DialogModule(
            dialogModule, npcLoc, players)
        holo.init()*/
    }
}