package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.ControlLogModule
import cn.inrhor.questengine.api.quest.QuestControlModule
import cn.inrhor.questengine.api.quest.QuestInnerModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.quest.manager.ControlManager
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.console
import taboolib.module.lang.sendLang
import java.io.File
import javax.swing.text.html.parser.ContentModel

object QuestFile {

    /**
     * 加载并注册任务
     */
    fun loadDialog() {
        val questFolder = GetFile.getFile("space/quest")
        val lists = questFolder.listFiles()?: return
        for (file in lists) {
            if (!file.isDirectory) continue
            checkRegQuest(file)
        }
    }

    private fun checkRegQuest(file: File) {
        val settingFile = file(file, "setting.yml")
        if (!settingFile.exists()) return
        val setting = yaml(settingFile)
        val questID = setting.getString("questID")?: return run {
            console().sendLang("QUEST-ERROR_FILE")
        }
        val name = setting.getString("name")?: "test"
        val startID = setting.getString("startInnerQuestID")?: "test"
        var modeType = ModeType.PERSONAL
        val modeTypeStr = setting.getString("mode.type")?: "personal"
        var modeAmount = -1
        var modeShareData = false
        if (modeTypeStr == "collaboration") {
            modeType = ModeType.COLLABORATION
            modeAmount = setting.getInt("mode.amount")
            modeShareData = setting.getBoolean("mode.shareData")
        }
        val acceptWay = setting.getString("accept.way")?: ""
        val maxQuantity = setting.getInt("accept.maxQuantity")
        val acceptCheck = setting.getInt("accept.check")
        val acceptCondition = setting.getStringList("accept.condition")
        val failCheck = setting.getInt("failure.check")
        val failCondition = setting.getStringList("failure.condition")
        val failKether = setting.getStringList("failure.kether")

        val innerQuestList = mutableListOf<QuestInnerModule>()

        val innerFolder = GetFile.getFile("space/quest/"+file.name+"/inner")
        val lists = innerFolder.listFiles()?: return run {
            console().sendLang("QUEST-ERROR_FILE", questID)
        }
        lists.forEach {
            val optionFile = file(it, "option.yml")
            if (!optionFile.exists()) return run {
                console().sendLang("QUEST-ERROR_FILE", questID)
            }
            innerQuestList.add(innerQuest(it, questID))
        }

        val questModule = QuestModule(questID, name, startID,
            modeType, modeAmount, modeShareData,
            acceptWay, maxQuantity,
            acceptCheck, acceptCondition,
            failCheck, failCondition, failKether,
            innerQuestList)

        QuestManager.register(questID, questModule)

    }

    private fun innerQuest(innerFile: File, questID: String): QuestInnerModule {
        val optionFile = file(innerFile, "option.yml")
        val option = yaml(optionFile)
        val innerQuestID = option.getString("innerQuestID")!!
        val nextInnerQuestID = option.getString("nextInnerQuestID")!!

        val description = option.getStringList("description")

        val controlFile = file(innerFile, "control.yml")
        val questControl = control(controlFile, questID, innerQuestID)

        val rewardFile = file(innerFile, "reward.yml")
        val questReward = reward(rewardFile, questID, innerQuestID)

        val targetFile = file(innerFile, "target.yml")
        val target = yaml(targetFile)
        val questTarget = TargetManager.getTargetList(target)

        return QuestInnerModule(innerQuestID, nextInnerQuestID, questControl, questReward, questTarget, description)
    }

    private fun file(file: File, path: String): File {
        return File(file.path + File.separator + path)
    }

    private fun yaml(file: File): FileConfiguration {
        return YamlConfiguration.loadConfiguration(file)
    }

    private fun reward(file: File, questID: String, innerQuestID: String): QuestReward {
        val finishReward = mutableMapOf<String, MutableList<String>>()
        var failReward = mutableListOf<String>()
        if (file.exists()) {
            val reward = yaml(file)
            for (rewardID in reward.getConfigurationSection("finishReward")!!.getKeys(false)) {
                finishReward[rewardID] = reward.getStringList("finishReward.$rewardID")
            }
            failReward = reward.getStringList("failReward")
        }
        return QuestReward(questID, innerQuestID, finishReward, failReward)
    }

    private fun control(file: File, questID: String, innerQuestID: String): QuestControlModule {
        val control = yaml(file)

        val hNode = "highest.log."
        val hLogEnable = control.getBoolean(hNode+"enable")
        val hLogType = control.getString(hNode+"type")?: "null"
        val hLogShell = control.getStringList(hNode+"reKether")
        val nNode = "highest.log."
        val nLogEnable = control.getBoolean(nNode+"enable")
        val nLogType = control.getString(nNode+"type")?: "null"
        val nLogShell = control.getStringList(nNode+"reKether")
        val logModule = ControlLogModule(hLogEnable, nLogEnable, hLogType, nLogType, hLogShell, nLogShell)

        val hControl = control.getStringList("highest.kether")
        val nControl = control.getStringList("normal.kether")
        val highestID = ControlManager.generateControlID(questID, innerQuestID, "highest")
        val normalID = ControlManager.generateControlID(questID, innerQuestID, "normal")
        return QuestControlModule(highestID, normalID, hControl, nControl, logModule)
    }


}