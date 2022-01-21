package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.control.*
import cn.inrhor.questengine.api.quest.module.inner.QuestInnerModule
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
            res.releaseResourceFile(main+"inner_1.yml", true)
            loadQuest()
        }
        for (file in lists) {
            if (!file.isDirectory) continue
            checkRegQuest(file)
        }
    }

    private fun checkRegQuest(file: File) {
        val settingFile = File(file.path + File.separator + "setting.yml")
        if (!settingFile.exists()) return
        val setting = yaml(settingFile)
        val questModule = setting.getObject<QuestModule>("quest", false)
        val questID = questModule.questID

        val descMap = mutableMapOf<String, List<String>>()
        if (setting.contains("desc")) {
            setting.getConfigurationSection("desc")!!.getKeys(false).forEach {
                descMap[it] = setting.getStringList("desc.$it")
            }
            questModule.descMap = descMap
        }

        val innerQuestList = mutableListOf<QuestInnerModule>()

        val innerFolder = FileUtil.getFile("space/quest/"+file.name)
        val lists = FileUtil.getFileList(innerFolder)
        for (it in lists) {
            val innerYaml = yaml(it)
            if (it.name == "setting.yml" && !innerYaml.contains("inner")) {
                continue
            }
            val innerModule = innerQuest(innerYaml, questID,  innerYaml.getObject("inner", false))
            innerQuestList.add(innerModule)
        }
        questModule.innerQuestList = innerQuestList
        QuestManager.register(questID, questModule, questModule.sort)
    }

    private fun innerQuest(innerYaml: Configuration, questID: String, innerModule: QuestInnerModule): QuestInnerModule {
        val innerQuestID = innerModule.id
        val questControls = if (innerYaml.contains("inner.control")) control(innerYaml, questID, innerQuestID) else mutableListOf()

        val questTarget = TargetManager.getTargetList(innerYaml)

        innerModule.questControl = questControls
        innerModule.questTargetList = questTarget

        return innerModule
    }

    private fun yaml(file: File): Configuration {
        return Configuration.loadFromFile(file)
    }

    private fun control(control: Configuration, questID: String, innerQuestID: String): MutableList<QuestControlOpen> {
        val node = "inner.control"
        val hNode = "$node.highest.log."
        val hLogEnable = control.getBoolean(hNode+"enable")
        val hLogType = control.getString(hNode+"type")?: "null"
        val hLogShell = control.getStringList(hNode+"recall")

        val nNode = "$node.normal.log."
        val nLogEnable = control.getBoolean(nNode+"enable")
        val nLogType = control.getString(nNode+"type")?: "null"
        val nLogShell = control.getStringList(nNode+"script")

        val hControl = control.getStringList("highest.script")
        val nControl = control.getStringList("normal.script")
        val highestID = ControlManager.generateControlID(questID, innerQuestID, "highest")
        val normalID = ControlManager.generateControlID(questID, innerQuestID, "normal")

        val hLogModule = ControlLogType(hLogEnable, hLogType, hLogShell)
        val highestModule = ControlHighestModule(highestID, hControl, hLogModule)
        val nLogModule = ControlLogType(nLogEnable, nLogType, nLogShell)
        val normalModule = ControlNormalModule(normalID, nControl, nLogModule)

        return mutableListOf(highestModule, normalModule)
    }

}