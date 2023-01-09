package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.register
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
        val list = FileUtil.getFileList(questFolder)
        if (list.isEmpty()) {
            console().sendLang("QUEST-NO_FILES", UtilString.pluginTag)
            QuestEngine.resource.releaseResourceFile("space/quest/crop1.yml", true)
            loadQuest()
        }
        FileUtil.getFileList(questFolder).forEach {
            checkRegQuest(it)
        }
    }

    private fun checkRegQuest(file: File) {
        val setting = Configuration.loadFromFile(file)
        val quest = setting.getObject<QuestFrame>("quest", false)
        quest.path = file.path
        quest.register()
    }

}