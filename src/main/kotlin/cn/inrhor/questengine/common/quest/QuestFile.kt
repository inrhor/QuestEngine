package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.api.quest.QuestMainModule
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

        val mainFolder = File(QuestEngine.plugin.dataFolder, "space/quest/"+file.name+"/main")
        GetFile().getFileList(mainFolder).forEach {
            val optionFile = file(file, "option.yml")
            if (!optionFile.exists()) return
            mainQuestList.add(mainQuest(it))
        }

        val questModule = QuestModule(questID, name, startID,
            modeType, modeAmount,
            acceptCheck, acceptCondition,
            failCheck, failCondition,
            mainQuestList)

    }

    private fun mainQuest(file: File): QuestMainModule {
        val option = yaml(file)
        val mainQuestID = option.getString("mainQuestID")
        return QuestMainModule(mainQuestID, )
    }

    private fun file(file: File, path: String): File {
        return File(file.path + File.separator + path)
    }

    private fun yaml(file: File): FileConfiguration {
        return YamlConfiguration.loadConfiguration(file)
    }

}