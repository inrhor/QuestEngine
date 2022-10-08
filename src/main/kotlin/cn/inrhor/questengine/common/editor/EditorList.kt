package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.common.editor.list.*
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import cn.inrhor.questengine.utlis.newLineList
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.platform.util.asLangText

object EditorList {
    /**
     * 可视化 - 任务列表
     */
    fun Player.editorListQuest(page: Int = 0) {
        val list = QuestManager.getQuestMap().values.toMutableList()
        EditorQuestList(this, asLangText("EDITOR-LIST-QUEST"))
            .editorBack(this, "/qen eval editor quest in home")
            .list(page, 7, list, true, "EDITOR-LIST-QUEST-INFO",
                "qen eval editor quest in list page {page}",
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-EDIT-META",
                    "EDITOR-LIST-QUEST-EDIT-HOVER",
                    "/qen eval quest select {questID} editor quest in edit home"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL-META",
                    "EDITOR-LIST-QUEST-DEL-HOVER",
                    "/qen eval quest select {questID} editor quest in del"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorAcceptCondition(questID: String, page: Int = 0) {
        listEdit(this, questID, questID.getQuestFrame().accept.condition,
            "ACCEPT", "CONDITION", "acceptcondition", page)
    }

    fun Player.editQuestNote(content: List<String>, questID: String, page: Int = 0) {
        contentEdit(content, questID, "EDITOR-QUEST-NOTE", "note", page)
    }

    fun Player.editGroupNote(content: List<String>, questID: String, page: Int = 0) {
        contentEdit(content, questID, "EDITOR-GROUP-NOTE", "groupnote", page)
    }

    fun Player.contentEdit(content: List<String>, questID: String, head: String, node: String, page: Int = 0) {
        val s = "qen eval quest select $questID editor quest in"
        val edit = "$s edit $node"
        val change = "/$s change $node to"
        EditorOfList(this, asLangText(head, questID))
            .editorBack(this, "/$s edit home")
            .listAdd(this, "$change add {head}")
            .list(page, 3, content, true, "EDITOR-CONTENT-LIST",
                "$edit page {page}",
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD-META",
                    "EDITOR-LIST-NEXT-ADD-HOVER",
                    "$change add {index}"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "$change del {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.contentEdit(content: String, questID: String, head: String, node: String, page: Int = 0) {
        contentEdit(content.newLineList(), questID, head, node, page)
    }

    fun listEdit(player: Player, questID: String, list: String, node: String, meta: String, cmd: String, page: Int = 0) {
        EditorOfList(player, player.asLangText("EDITOR-$node-$meta-LIST", questID))
            .editorBack(player, "/qen eval quest select $questID editor quest in edit home")
            .listAdd(player, "/qen eval quest select $questID editor quest in change $cmd to add {head}")
            .list(page, 3, list.newLineList(), true, "EDITOR-$meta-LIST",
                "qen eval quest select $questID editor quest in edit $cmd page {page}",
                EditorListModule.EditorButton("EDITOR-$meta-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD-META",
                    "EDITOR-LIST-NEXT-ADD-HOVER",
                    "/qen eval quest select $questID editor quest in change $cmd to add {index}"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID editor quest in change $cmd to del {index}"))
            .json.sendTo(adaptPlayer(player))
    }

    fun Player.editorTargetCondition(questID: String, targetID: String, page: Int = 0) {
        val t = targetID.getTargetFrame(questID)
        val list = t.condition
        EditorOfList(this, asLangText("EDITOR-TARGET-CONDITION", questID, targetID))
            .editorBack(this, "/qen eval quest select $questID target select $targetID editor target in edit home")
            .listAdd(this, "/qen eval quest select $questID target select $targetID editor target in change condition to add {head}")
            .list(page, 3, list.newLineList(), true, "EDITOR-CONDITION-LIST",
                "qen eval quest select $questID target select $targetID editor target in edit condition page {page}",
                EditorListModule.EditorButton("EDITOR-CONDITION-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD-META",
                    "EDITOR-LIST-NEXT-ADD-HOVER",
                    "/qen eval quest select $questID target select $targetID editor target in change condition to add {index}"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID target select $targetID editor target in change condition to del {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun EditorListModule.listAdd(player: Player, command: String): EditorListModule {
        add(player.asLangText("EDITOR-LIST-ADD"),
            EditorListModule.EditorButton(player.asLangText("EDITOR-LIST-ADD-META"),
                player.asLangText("EDITOR-LIST-ADD-HOVER"),
                command))
        return this
    }

    fun EditorListModule.editorBack(player: Player, command: String): EditorListModule {
        add(player.asLangText("EDITOR-BACK"),
            EditorListModule.EditorButton(player.asLangText("EDITOR-BACK-META"),
                player.asLangText("EDITOR-BACK-HOVER"),
                command))
        return this
    }

    fun Player.editorTargetList(questID: String, page: Int = 0) {
        val quest = questID.getQuestFrame()
        EditorTargetList(this, asLangText("EDITOR-TARGET", questID))
            .editorBack(this, "/qen eval quest select $questID editor quest in edit home")
            .add(asLangText("EDITOR-TARGET-ADD"),
                EditorListModule.EditorButton(asLangText("EDITOR-TARGET-ADD-META"),
                    asLangText("EDITOR-TARGET-ADD-HOVER"),
                    "/qen eval quest select $questID editor target in add"))
            .list(page, 7, quest.target, true,
                "EDITOR-TARGET-LIST",
                "qen eval quest select $questID editor target in list page {page}",
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT"),
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT-META",
                "EDITOR-TARGET-EDIT-HOVER",
                "/qen eval quest select $questID target select {targetID} editor target in edit home"),
                EditorListModule.EditorButton("EDITOR-TARGET-DEL"),
                EditorListModule.EditorButton("EDITOR-TARGET-DEL-META",
                    asLangText("EDITOR-TARGET-DEL-HOVER"),
                    "/qen eval quest select $questID target select {targetID} editor target in del"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.selectTargetList(questID: String, targetID: String, page: Int = 0) {
        EditorSelTarget(this, asLangText("EDITOR-SELECT-TARGET", questID, targetID))
            .editorBack(this,
                "/qen eval quest select $questID target select $targetID editor target in edit home")
            .list(page, 7, RegisterTarget.saveTarget.map { it.key }, true, "EDITOR-SELECT",
                "qen eval quest select $questID target select $targetID editor target in sel list page {page}",
                EditorListModule.EditorButton("EDITOR-SELECT-TARGET-SEL",
                    "EDITOR-SELECT-TARGET-SEL-HOVER",
                    "/qen eval quest select $questID target select $targetID editor target in change name to {targetName}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorNodeList(questID: String, target: TargetFrame, node: String, page: Int = 0) {
        val list = target.nodeMeta(node)?: mutableListOf()
        val id = target.id
        EditorOfList(this, asLangText("EDITOR-TARGET-LIST-UI-NODE",
            questID, id, asLangText("EDITOR-TARGET-LIST-NODE-${node.uppercase()}")), other = arrayOf(target.event, node))
            .editorBack(this, "/qen eval quest select $questID target select $id editor target in edit home")
            .listAdd(this, "/qen eval quest select $questID target select $id editor target in change node to '$node' add {head}")
            .list(page, 7, list, true, "EDITOR-TARGET-LIST-FOR-NODE",
                "/qen eval quest select $questID target select $id editor target in sel node page {page} to '$node'",
                EditorListModule.EditorButton("EDITOR-CONDITION-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-NEXT-ADD-META",
                    "EDITOR-LIST-NEXT-ADD-HOVER",
                    "/qen eval quest select $questID target select $id editor target in change node to '$node' add {index}"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID target select $id editor target in change node to '$node' del {index}"))
            .json.sendTo(adaptPlayer(this))
    }
}