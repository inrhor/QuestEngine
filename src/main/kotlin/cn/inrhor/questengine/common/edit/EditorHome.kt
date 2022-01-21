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
            .append("   "+this.asLangText("EDITOR-HOME"))
            .newLine()
            .newLine()
            .append("      "+this.asLangText("EDITOR-HOME-LIST"))
            .append("  "+this.asLangText("EDITOR-HOME-LIST-META"))
            .hoverText(this.asLangText("EDITOR-HOME-LIST-HOVER"))
            .runCommand("/qen editor quest list")
            .newLine()
            .append("      "+this.asLangText("EDITOR-HOME-ADD"))
            .append("  "+this.asLangText("EDITOR-HOME-ADD-META"))
            .hoverText(this.asLangText("EDITOR-HOME-ADD-HOVER"))
            .runCommand("/qen editor quest add quest")
            .newLine()
            .sendTo(adaptPlayer(this))
    }
}