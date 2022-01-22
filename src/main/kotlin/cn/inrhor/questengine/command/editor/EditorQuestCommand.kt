package cn.inrhor.questengine.command.editor

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.module.main.QuestModule
import cn.inrhor.questengine.common.edit.EditorHome.editorHomeQuest
import cn.inrhor.questengine.common.edit.EditorList.editorListQuest
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.setObject
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText
import taboolib.platform.util.sendLang
import java.io.File

internal object EditorQuestCommand {

    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->
            sender.editorHomeQuest()
        }
    }

    @CommandBody
    val list = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                val args = argument.split(" ")
                sender.editorListQuest(args[0].toInt())
            }
        }
        execute<Player> { sender, _, _ ->
            sender.editorListQuest()
        }
    }

    @CommandBody
    val add = subCommand {
        execute<Player> { sender, _, _ ->
            sender.inputSign(arrayOf(sender.asLangText("EDITOR-PLEASE-QUEST-ID"))) {
                val questID = it[1].replace(" ", "")
                if (questID.isEmpty() && QuestManager.questMap.containsKey(questID)) {
                    sender.sendLang("QUEST-ERROR-ID")
                    return@inputSign
                }
                val file = newFile(File(QuestEngine.plugin.dataFolder, "/space/quest/$questID"), folder = true)
                val questModule = QuestModule(questID = questID)
                val setting = newFile(file.path+"/setting.yml")
                val yaml = Configuration.loadFromFile(setting)
                yaml.setObject("quest", questModule)
                yaml.saveToFile(setting)
                QuestManager.register(questID, questModule)
            }
        }
    }

    @CommandBody
    val del = subCommand {
        dynamic {
            execute<Player> { _, _, argument ->
                val args = argument.split(" ")
                val questID = args[0]
                QuestManager.delQuest(questID)
            }
        }
    }

}