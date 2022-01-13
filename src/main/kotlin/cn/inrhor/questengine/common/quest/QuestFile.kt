package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.control.*
import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
import cn.inrhor.questengine.api.quest.module.inner.QuestReward
import cn.inrhor.questengine.api.quest.module.main.QuestModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
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
            val main = "space/quest/cropQuest/"
            val res = QuestEngine.resource
            res.releaseResourceFile(main+"setting.yml", true)
            val inner = "${main}inner/crop_start/"
            res.releaseResourceFile(inner+"control.yml", true)
            res.releaseResourceFile(inner+"option.yml", true)
            res.releaseResourceFile(inner+"reward.yml", true)
            res.releaseResourceFile(inner+"target.yml", true)
            loadQuest()
        }
        for (file in lists) {
            if (!file.isDirectory) continue
            checkRegQuest(file)
        }
    }

    private fun checkRegQuest(file: File) {
        val settingFile = file(file, "setting.yml")
        if (!settingFile.exists()) return
        val setting = yaml(settingFile)
        val questModule = setting.getObject<QuestModule>("quest", false)
        val questID = questModule.questID

        val descMap = mutableMapOf<String, List<String>>()
        setting.getConfigurationSection("desc")!!.getKeys(false).forEach {
            descMap[it] = setting.getStringList("desc.$it")
        }
        questModule.descMap = descMap

        val innerQuestList = mutableListOf<QuestInnerModule>()

        val innerFolder = FileUtil.getFile("space/quest/"+file.name+"/inner")
        val lists = innerFolder.listFiles()?: return run {
            console().sendLang("QUEST-ERROR_FILE", questID)
        }
        for (it in lists) {
            val optionFile = file(it, "option.yml")
            if (!optionFile.exists()) return run {
                console().sendLang("QUEST-ERROR_FILE", questID)
            }
            val innerModule = innerQuest(
                it, questID, yaml(optionFile).getObject("inner", false))

            innerQuestList.add(innerModule)
        }
        questModule.innerQuestList = innerQuestList
        QuestManager.register(questID, questModule, questModule.sort)
    }

    private fun innerQuest(innerFile: File, questID: String, innerModule: QuestInnerModule): QuestInnerModule {
        val innerQuestID = innerModule.id
        val controlFile = file(innerFile, "control.yml")
        val questControls = if (controlFile.exists()) control(controlFile, questID, innerQuestID) else mutableListOf()

        val rewardFile = file(innerFile, "reward.yml")
        val questReward = yaml(rewardFile).getObject<QuestReward>("reward", false)

        val targetFile = file(innerFile, "target.yml")
        val target = yaml(targetFile)
        val questTarget = TargetManager.getTargetList(target)

        innerModule.questControls = questControls
        innerModule.questReward = questReward
        innerModule.questTargetList = questTarget

        return innerModule
    }

    private fun file(file: File, path: String): File {
        return File(file.path + File.separator + path)
    }

    private fun yaml(file: File): Configuration {
        return Configuration.loadFromFile(file)
    }

    private fun control(file: File, questID: String, innerQuestID: String): MutableList<QuestControlOpen> {
        val control = yaml(file)

        val hNode = "highest.log."
        val hLogEnable = control.getBoolean(hNode+"enable")
        val hLogType = control.getString(hNode+"type")?: "null"
        val hLogShell = control.getStringList(hNode+"reKether")

        val nNode = "normal.log."
        val nLogEnable = control.getBoolean(nNode+"enable")
        val nLogType = control.getString(nNode+"type")?: "null"
        val nLogShell = control.getStringList(nNode+"reKether")

        val hControl = control.getStringList("highest.kether")
        val nControl = control.getStringList("normal.kether")
        val highestID = ControlManager.generateControlID(questID, innerQuestID, "highest")
        val normalID = ControlManager.generateControlID(questID, innerQuestID, "normal")

        val hLogModule = ControlLogType(hLogEnable, hLogType, hLogShell)
        val highestModule = ControlHighestModule(highestID, hControl, hLogModule)
        val nLogModule = ControlLogType(nLogEnable, nLogType, nLogShell)
        val normalModule = ControlNormalModule(normalID, nControl, nLogModule)

        return mutableListOf(highestModule, normalModule)
    }

}