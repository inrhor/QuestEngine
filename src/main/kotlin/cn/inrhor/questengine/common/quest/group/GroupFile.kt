package cn.inrhor.questengine.common.quest.group

import cn.inrhor.questengine.api.quest.GroupFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject

object GroupFile {

    fun load() {
        val dialogFolder = FileUtil.getFile("space/group/", "GROUP-NO_FILES", true,
            "example")

        FileUtil.getFileList(dialogFolder).forEach{
            val yaml = Configuration.loadFromFile(it)
            yaml.getConfigurationSection("")?.getKeys(false)?.forEach { i->
                val g: GroupFrame = yaml.getObject(i, false)
                g.id = i
                QuestManager.groupMap[i] = g
                g.quest.forEach { q ->
                    val quest = q.getQuestFrame()
                    if (quest != null) g.quests.add(quest)
                }
            }
        }
    }

}