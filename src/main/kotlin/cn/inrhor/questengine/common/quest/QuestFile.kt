package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.api.quest.QuestMainModule
import cn.inrhor.questengine.api.quest.QuestManager
import cn.inrhor.questengine.api.quest.QuestModule
import cn.inrhor.questengine.api.quest.QuestSubModule
import cn.inrhor.questengine.utlis.file.GetFile
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object QuestFile {

    /**
     * 加载并注册任务
     */
    fun loadDialog() {
        val questFolder = GetFile().getFile("space/quest", "DIALOG.NO_FILES", false)
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
        if (!setting.contains("questID")) {
            return
        }
        val questID = setting.getString("questID")!!
        val name = setting.getString("name")?: "test"
        val startID = setting.getString("startMainQuestID")?: "test"
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

        val mainQuestList = mutableListOf<QuestMainModule>()

        val mainFolder = GetFile().getFile("space/quest/"+file.name+"/main", "DIALOG.NO_FILES", false)
        val lists = mainFolder.listFiles()?: return
        lists.forEach {
            val optionFile = file(it, "option.yml")
            if (!optionFile.exists()) return
            mainQuestList.add(mainQuest(file, it, questID)!!)
        }

        val questModule = QuestModule(questID, name, startID,
            modeType, modeAmount, modeShareData,
            acceptCheck, acceptCondition,
            failCheck, failCondition,
            mainQuestList)

        QuestManager.register(questID, questModule)

    }

    private fun mainQuest(file: File, mainFile: File, questID: String): QuestMainModule? {
        val optionFile = file(mainFile, "option.yml")
        val option = yaml(optionFile)
        val mainQuestID = option.getString("mainQuestID")!!
        val nextMainQuestID = option.getString("nextMainQuestID")!!

        val controlFile = file(mainFile, "control.yml")
        val questControl = control(controlFile, questID, mainQuestID, "")

        val rewardFile = file(mainFile, "reward.yml")
        val questReward = reward(rewardFile, questID, mainQuestID, "")

        val targetFile = file(mainFile, "target.yml")
        val target = yaml(targetFile)
        val questTarget = TargetManager.getTargetList(target)

        val subQuestList = mutableListOf<QuestSubModule>()

        val subFolder = GetFile().getFile("space/quest/"+file.name+"/main/"+mainFile.name+"/sub", "DIALOG.NO_FILES", false)
        val lists = subFolder.listFiles()?: return null
        lists.forEach {
            val optionSubFile = file(it, "option.yml")
            if (optionSubFile.exists()) {
                val optionSub = yaml(optionSubFile)
                val subQuestID = optionSub.getString("subQuestID")!!

                val controlSubFile = file(it, "control.yml")
                val questControlSub = control(controlSubFile, questID, mainQuestID, subQuestID)

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

    private fun control(file: File, questID: String, mainQuestID: String, subQuestID: String): QuestControl {
        val control = yaml(file)
        val controlKether = control.getStringList("kether")
        val id = QuestManager.generateControlID(questID, mainQuestID, subQuestID)
        return QuestControl(id, controlKether)
    }


}