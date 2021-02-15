package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.dialog.location.LocationTool
import cn.inrhor.questengine.common.hologram.IHolo
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.file.GetFile
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
        private var dialogFileMap: HashMap<String, DialogCube> = LinkedHashMap()
    }

    /**
     * 注册对话
     */
    fun register(dialogID: String, dialogCube: DialogCube) {
        if (exist(dialogID)) {
            TLocale.sendToConsole("DIALOG.EXIST_DIALOG_ID", UseString.pluginTag, dialogID)
            return
        }
        dialogFileMap[dialogID] = dialogCube
    }

    /**
     * 加载并注册对话
     */
    fun loadDialog() {
        val dialogFolder = GetFile().getFile("dialog", "DIALOG.NO_FILES")
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
     * 根据NPCID并判断玩家集合是否满足条件，返回DialogCube
     */
    fun returnDialogHolo(players: MutableSet<Player>, npcID: String): DialogCube? {
        for ((_, dialogCube) in dialogFileMap) {
            if (dialogCube.npcID != npcID) continue
            if (KetherHandler.evalBooleanSet(players, dialogCube.condition)) return dialogCube
        }
        return null
    }

    fun sendDialogHolo(players: MutableSet<Player>, npcID: String, npcLoc: Location) {
        val dialogCube = DialogManager().returnDialogHolo(players, npcID)!!
        val holo = IHolo(
            dialogCube, npcLoc, players)
        holo.init()
    }
}