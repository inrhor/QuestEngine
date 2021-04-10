package cn.inrhor.questengine.common.dialog

import cn.inrhor.questengine.common.dialog.animation.parser.ItemParser
import cn.inrhor.questengine.common.dialog.animation.parser.TextAnimation
import cn.inrhor.questengine.common.dialog.cube.DialogCube
import cn.inrhor.questengine.common.dialog.cube.ReplyCube
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

            val npcID = cfs.getString("npcID")!!
            val condition = cfs.getStringList("condition")
            val dialog = cfs.getStringList("dialog")

            val textAnimations = mutableListOf<String>()
            val itemAnimations = mutableListOf<String>()
            for (i in dialog) {
                if (i.startsWith("text")) {
                    textAnimations.add(i.substring(0, i.indexOf("text ")))
                    break
                }
                if (i.startsWith("itemNormal")) {
                    itemAnimations.add(i)
                    break
                }
            }

            val dtAnimation = TextAnimation(textAnimations)
            val diParser = ItemParser(itemAnimations)

            val dialogCube = DialogCube(dialogID, npcID, condition, dialog, dtAnimation, diParser)

            if (cfs.contains("reply")) {
                val replySfc = cfs.getConfigurationSection("reply")!!
                if (replySfc.getKeys(false).isNotEmpty()) {
                    for (replyID in replySfc.getKeys(false)) {
                        val content = replySfc.getStringList("$replyID.content")
                        val script = replySfc.getStringList("$replyID.script")
                        val replyCube = ReplyCube(replyID, content, script)
                        dialogCube.replyCubeList.add(replyCube)
                    }
                }
            }

            DialogManager().register(dialogID, dialogCube)
        }
    }

}