package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.existQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.register
import cn.inrhor.questengine.common.quest.manager.QuestManager.waitRegister
import cn.inrhor.questengine.utlis.UtilString
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.common.platform.function.*
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import taboolib.module.lang.sendLang
import java.io.File

object QuestFile {

    /**
     * 加载并注册任务
     */
    fun loadQuest() {
        val questFolder = FileUtil.getFile("space/quest")
        val lists = questFolder.listFiles()?: return run {
            console().sendLang("QUEST-NO_FILES", UtilString.pluginTag)
            QuestEngine.resource.releaseResourceFile("space/quest/crop1.yml", true)
            loadQuest()
        }
        lists.forEach {
            checkRegQuest(it)
        }
        QuestManager.extendsQuest.forEach {
            val ext = it.group.extends
            if (ext.existQuestFrame()) {
                it.group = ext.getQuestFrame().group
            }
        }
        QuestManager.extendsQuest.clear()
    }

    private fun checkRegQuest(file: File) {
        val setting = Configuration.loadFromFile(file)
        val quest = setting.getObject<QuestFrame>("quest", false)
        val group = quest.group
        val extend = group.extends
        quest.path = file.path
        if (extend.isNotEmpty()) {
            if (extend.existQuestFrame()) {
                quest.group = extend.getQuestFrame().group
            }else {
                quest.waitRegister()
            }
        }else {
            quest.register()
        }
    }

}