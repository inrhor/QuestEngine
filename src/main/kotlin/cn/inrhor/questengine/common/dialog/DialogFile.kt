package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextAnimation
import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.dialog.cube.ReplyCube
import cn.inrhor.questengine.common.kether.KetherHandler
import cn.inrhor.questengine.utlis.public.UseString
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class DialogFile {

    /**
     * 检查配置和注册对话
     */
    fun checkRegDialog(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        if (yaml.getKeys(false).isEmpty()) {
            TLocale.sendToConsole("DIALOG.EMPTY_CONTENT", UseString.pluginTag, file.name)
            return
        }
        for (dialogID in yaml.getKeys(false)) {
            val cfs = yaml.getConfigurationSection(dialogID)!!
            if (!cfs.contains("npcID")) {
                return
            }
            if (!cfs.contains("condition")) {
                return
            }
            val ownSec = "dialog.own."
            if (!cfs.contains(ownSec+"text.addLocation")) {
                return
            }
            if (!cfs.contains(ownSec+"text.content")) {
                return
            }
            if (!cfs.contains(ownSec+"item.addLocation")) {
                return
            }
            if (!cfs.contains(ownSec+"item.content")) {
                return
            }

            val npcID = cfs.getString("npcID")!!
            val condition = cfs.getStringList("condition")
            val ownTextFixedLoc = KetherHandler.evalFixedLoc(cfs.getString(ownSec+"text.addLocation")!!)
            val ownTextInitContent = cfs.getStringList(ownSec+"text.content")
            val ownItemFixedLoc = KetherHandler.evalFixedLoc(cfs.getString(ownSec+"item.addLocation")!!)
            val ownItemInitContent = cfs.getStringList(ownSec+"item.content")

            val ownTextAnimation = TextAnimation(ownTextInitContent)
            ownTextAnimation.init()
            val ownItemAnimation = ItemParser(ownItemInitContent)
            ownItemAnimation.init()

            val frame = cfs.getInt(ownSec+"frame")

            val dialogCube = DialogCube(dialogID, npcID, condition,
                ownTextFixedLoc, ownTextInitContent, ownTextAnimation,
                ownItemFixedLoc, ownItemAnimation,
                frame)

            val replyPath = "dialog.reply"
            if (cfs.contains("dialog.reply")) {
                val replySfc = cfs.getConfigurationSection(replyPath)!!
                if (replySfc.getKeys(false).isNotEmpty()) {
                    for (replyID in replySfc.getKeys(false)) {
                        val textAddLoc = KetherHandler.evalFixedLoc(
                            replySfc.getString("$replyID.text.addLocation")!!)
                        val textContent = replySfc.getStringList("$replyID.text.content")
                        val itemAddLoc = KetherHandler.evalFixedLoc(
                            replySfc.getString("$replyID.item.addLocation")!!)
                        val itemContent = replySfc.getStringList("$replyID.item.content")
                        val replyItemAnimation = ItemParser(itemContent)
                        replyItemAnimation.init()
                        val replyCube = ReplyCube(replyID, textAddLoc, textContent, itemAddLoc, replyItemAnimation)
                        dialogCube.replyCubeList.add(replyCube)
                    }
                }
            }

            DialogManager().register(dialogID, dialogCube)
        }
    }

}