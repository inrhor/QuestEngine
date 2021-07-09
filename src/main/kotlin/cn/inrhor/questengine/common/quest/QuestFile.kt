package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestMainModule
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.api.quest.QuestSubModule
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object QuestFile {

    fun checkRegQuest(file: File) {
        val settingFile = file(file, "setting.yml")
        if (!settingFile.exists()) return
        val setting = yaml(settingFile)
        if (!setting.contains("questID")) {
            return
        }
        val questID = setting.getString("questID")!!
        val name = setting.getString("name")?: "test"
        val startID = setting.getString("startMainQuestID")?: "test"
        val modeType = setting.getString("mode.type")?: "personal"
        var modeAmount = -1
        if (modeType == "team") {
            modeAmount = setting.getInt("mode.amount")
        }
        val acceptCheck = setting.getInt("accept.check")
        val acceptCondition = setting.getStringList("accept.condition")
        val failCheck = setting.getInt("failure.check")
        val failCondition = setting.getStringList("failure.condition")

        val mainQuestList = mutableListOf<QuestMainModule>()

        val mainFolder = File(QuestEngine.plugin.dataFolder,
            "space/quest/"+file.name+"/main")
        GetFile().getFileList(mainFolder).forEach {
            val optionFile = file(file, "option.yml")
            if (!optionFile.exists()) return
            mainQuestList.add(mainQuest(file, it, questID)!!)
        }

        val questModule = QuestModule(questID, name, startID,
            modeType, modeAmount,
            acceptCheck, acceptCondition,
            failCheck, failCondition,
            mainQuestList)

        QuestManager().register(questID, questModule)

    }

    private fun mainQuest(file: File, mainFile: File, questID: String): QuestMainModule? {
        val option = yaml(mainFile)
        val mainQuestID = option.getString("mainQuestID")!!
        val nextMainQuestID = option.getString("nextMainQuestID")!!

        val controlFile = file(mainFile, "control.yml")
        val questControl = control(controlFile, questID, mainQuestID)

        val rewardFile = file(mainFile, "reward.yml")
        val questReward = reward(rewardFile, questID, mainQuestID, "")

        val targetFile = file(mainFile, "target.yml")
        val target = yaml(targetFile)
        val questTarget = TargetManager.getTargetList(target)

        val subQuestList = mutableListOf<QuestSubModule>()

        val subFolder = File(QuestEngine.plugin.dataFolder,
            "space/quest/"+file.name+"/main/"+mainFile.name+"/sub")
        GetFile().getFileList(subFolder).forEach {
            val optionFile = file(it, "option.yml")
            val optionSub = yaml(it)
            if (optionFile.exists()) {
                val subQuestID = optionSub.getString("subQuestID")!!

                val controlSubFile = file(it, "control.yml")
                val questControlSub = control(controlSubFile, questID, subQuestID)

                val rewardSubFile = file(it, "reward.yml")
                val questRewardSub = reward(rewardSubFile, questID, mainQuestID, subQuestID)

                val targetSubFile = file(it, "target.yml")
                val targetSub = yaml(targetSubFile)
                val questTargetSub = TargetManager.getTargetList(targetSub)

                val questSubModule = QuestSubModule(subQuestID, questControlSub,
                    questRewardSub, questTargetSub)

                subQuestList.add(questSubModule)
            }
        }
        return QuestMainModule(mainQuestID, nextMainQuestID, subQuestList, questControl, questReward, questTarget)
    }

    private fun file(file: File, path: String): File {
        return File(file.path + File.separator + path)
    }

    private fun yaml(file: File): FileConfiguration {
        return YamlConfiguration.loadConfiguration(file)
    }

    private fun reward(file: File, questID: String, mainQuestID: String, subQuestID: String): QuestReward {
        val finishReward = mutableMapOf<String, MutableList<String>>()
        var failReward = mutableListOf<String>()
        val rewardFile = file(file, "reward.yml")
        if (rewardFile.exists()) {
            val reward = yaml(rewardFile)
            for (rewardID in reward.getConfigurationSection("finishReward")!!.getKeys(false)) {
                finishReward[rewardID] = reward.getStringList("finishReward.$rewardID")
            }
            failReward = reward.getStringList("failReward")
        }
        return QuestReward(questID, mainQuestID, subQuestID, finishReward, failReward)
    }

    private fun control(file: File, questID: String, id: String): QuestControl {
        val control = yaml(file)
        val controlKether = control.getStringList("kether")
        return QuestControl(questID, id, controlKether)
    }


}