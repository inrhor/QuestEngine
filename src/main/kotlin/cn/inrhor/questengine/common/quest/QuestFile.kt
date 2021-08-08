package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.QuestInnerModule
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.common.quest.manager.TargetManager
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.console
import taboolib.module.lang.sendLang
import java.io.File

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
        val acceptCheck = setting.getInt("accept.check")
        val acceptCondition = setting.getStringList("accept.condition")
        val failCheck = setting.getInt("failure.check")
        val failCondition = setting.getStringList("failure.condition")

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
            acceptCheck, acceptCondition,
            failCheck, failCondition,
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

    private fun control(file: File, questID: String, innerQuestID: String): QuestControl {
        val control = yaml(file)
        val highestControl = control.getStringList("highest.kether")
        val normalControl = control.getStringList("normal.kether")
        val highestID = QuestManager.generateControlID(questID, innerQuestID, "highest")
        val normalID = QuestManager.generateControlID(questID, innerQuestID, "normal")
        return QuestControl(highestID, normalID, highestControl, normalControl)
    }


}