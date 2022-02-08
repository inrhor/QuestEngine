package cn.inrhor.questengine.common.edit

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.chat.TellrawJson
import taboolib.platform.util.asLangText

object EditorHome {

    /**
     * 可视化 - 主页
     */
    fun Player.editorHome() {
        TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-HOME"))
            .newLine()
            .newLine()
            .append("      "+asLangText("EDITOR-HOME-QUEST-BUTTON"))
            .hoverText(asLangText("EDITOR-HOME-QUEST-BUTTON-HOVER"))
            .runCommand("/qen editor quest home")
            .newLine()
            .append("      "+asLangText("EDITOR-HOME-DIALOG-BUTTON"))
            .hoverText(asLangText("EDITOR-HOME-DIALOG-BUTTON-HOVER"))
            .runCommand("/qen editor dialog home")
            .newLine()
            .sendTo(adaptPlayer(this))
    }
    
    /**
     * 可视化 - 任务主页
     */
    fun Player.editorHomeQuest() {
        TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-HOME-QUEST"))
            .newLine()
            .newLine()
            .append("      "+asLangText("EDITOR-HOME-QUEST-LIST"))
            .append("  "+asLangText("EDITOR-HOME-QUEST-LIST-META"))
            .hoverText(asLangText("EDITOR-HOME-QUEST-LIST-HOVER"))
            .runCommand("/qen editor quest list")
            .newLine()
            .append("      "+asLangText("EDITOR-HOME-QUEST-ADD"))
            .append("  "+asLangText("EDITOR-HOME-QUEST-ADD-META"))
            .hoverText(asLangText("EDITOR-HOME-QUEST-ADD-HOVER"))
            .runCommand("/qen editor quest add")
            .newLine()
            .sendTo(adaptPlayer(this))
    }
}