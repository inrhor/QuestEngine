package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.module.inner.QuestTarget
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.common.editor.list.*
import cn.inrhor.questengine.common.quest.manager.QuestManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.platform.util.asLangText

object EditorList {
    /**
     * 可视化 - 任务列表
     */
    fun Player.editorListQuest(page: Int = 0) {
        val list = QuestManager.questMap.values.toMutableList()
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

    fun Player.editorListInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-LIST-INNER", questID))
            .editorBack(this, "/qen eval quest select $questID editor quest in edit home")
            .add(asLangText("EDITOR-LIST-INNER-ADD"),
            EditorListModule.EditorButton(asLangText("EDITOR-LIST-INNER-ADD-META"),
                asLangText("EDITOR-LIST-INNER-ADD-HOVER"),
            "/qen eval quest select $questID editor inner in add"))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-LIST-INNER-INFO",
                "qen eval editor quest in list page {page}",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT-META",
                    "EDITOR-LIST-INNER-EDIT-HOVER",
                    "/qen eval quest select $questID inner select {innerID} editor inner in edit home"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL-META",
                    "EDITOR-LIST-INNER-DEL-HOVER",
                    "/qen eval quest select $questID inner select {innerID} editor inner in del"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorStartInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-EDIT-QUEST-INNER-START", questID))
            .editorBack(this, "/qen eval quest select $questID editor quest in edit home")
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen eval quest select $questID editor quest in edit page {page}",
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE"),
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE-META",
                    "EDITOR-EDIT-QUEST-START-STATE-HOVER",
                    "/qen eval quest select $questID editor quest in change start to {innerID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorAcceptCondition(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.accept.condition,
            "ACCEPT", "CONDITION", "acceptcondition", page)
    }

    fun Player.editorFailCondition(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.failure.condition,
            "FAIL", "CONDITION","failcondition", page)
    }

    fun Player.editorFailScript(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        listEditDel(this, questID, questModule.failure.script,
            "FAIL", "SCRIPT","failscript", page)
    }

    fun listEditDel(player: Player, questID: String, list: List<String>, node: String, meta: String, cmd: String, page: Int = 0) {
        EditorOfList(player, player.asLangText("EDITOR-$node-$meta-LIST", questID))
            .editorBack(player, "/qen eval quest select $questID editor quest in edit home")
            .list(page, 3, list, true, "EDITOR-$meta-LIST",
                "qen eval quest select $questID editor quest in edit $cmd page {page}",
                EditorListModule.EditorButton("EDITOR-$meta-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID editor quest in change $cmd to {index}"))
            .json.sendTo(adaptPlayer(player))
    }

    fun Player.editorTargetCondition(questID: String, innerID: String, targetID: String, page: Int = 0) {
        val t = QuestManager.getTargetModule(questID, innerID, targetID)?: return
        val list = t.condition
        EditorOfList(this, asLangText("EDITOR-TARGET-CONDITION-LIST", questID, innerID, targetID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in edit home")
            .list(page, 3, list, true, "EDITOR-TARGET-CONDITION-LIST",
                "qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in edit condition page {page}",
                EditorListModule.EditorButton("EDITOR-TARGET-CONDITION-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in change condition to {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun EditorListModule.editorBack(player: Player, command: String): EditorListModule {
        add(player.asLangText("EDITOR-BACK"),
            EditorListModule.EditorButton(player.asLangText("EDITOR-BACK-META"),
                player.asLangText("EDITOR-BACK-HOVER"),
                command))
        return this
    }

    fun Player.editorNextInner(questID: String, innerID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-EDIT-INNER-NEXT", questID, innerID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen eval quest select $questID inner select $innerID editor inner in edit nextinner page {page}",
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-CHOOSE"),
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-META",
                    "EDITOR-EDIT-INNER-NEXT-HOVER",
                    "/qen eval quest select $questID inner select $innerID editor inner in change nextinner to {innerID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorInnerDesc(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-EDIT-INNER-NOTE", questID, innerID), empty = "  ")
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .add(asLangText("EDITOR-LIST-INNER-DESC-ADD"),
                EditorListModule.EditorButton(asLangText("EDITOR-LIST-INNER-DESC-ADD-META"),
                    asLangText("EDITOR-LIST-INNER-DESC-ADD-HOVER"),
                    "/qen eval quest select $questID inner select $innerID editor inner in change desc add to {head}"))
            .list(page, 5, inner.description, true, "EDITOR-LIST-INNER-NOTE-LIST",
                "qen eval quest select $questID inner select $innerID editor inner in edit desc page {page}",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD-META",
                    "EDITOR-LIST-INNER-NOTE-ADD-HOVER",
                    "/qen eval quest select $questID inner select $innerID editor inner in change desc add to {index}"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL-META",
                    "EDITOR-LIST-INNER-NOTE-DEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID editor inner in change desc del to {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorTargetList(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorTargetList(this, asLangText("EDITOR-TARGET", questID, innerID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .add(asLangText("EDITOR-TARGET-ADD"),
                EditorListModule.EditorButton(asLangText("EDITOR-TARGET-ADD-META"),
                    asLangText("EDITOR-TARGET-ADD-HOVER"),
                    "/qen eval quest select $questID inner select $innerID editor target in add"))
            .list(page, 7, inner.target, true,
                "EDITOR-TARGET-LIST",
                "qen eval quest select $questID inner select $innerID editor target in list page {page}",
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT"),
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT-META",
                "EDITOR-TARGET-EDIT-HOVER",
                "/qen eval quest select $questID inner select $innerID innerTarget select {targetID} editor target in edit home"),
                EditorListModule.EditorButton("EDITOR-TARGET-DEL"),
                EditorListModule.EditorButton("EDITOR-TARGET-DEL-META",
                    asLangText("EDITOR-TARGET-DEL-HOVER"),
                    "/qen eval quest select $questID inner select $innerID innerTarget select {targetID} editor target in del"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorRewardList(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorRewardList(this, asLangText("EDITOR-FINISH_REWARD", questID, innerID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .list(page, 7, inner.reward.finish, true, "EDITOR-FINISH_REWARD-LIST",
            "qen eval quest select $questID inner select $innerID editor reward in list page {page}",
            EditorListModule.EditorButton("EDITOR-EDIT-FINISH_REWARD-EDIT"),
            EditorListModule.EditorButton("EDITOR-EDIT-FINISH_REWARD-EDIT-META",
            "EDITOR-EDIT-FINISH_REWARD-EDIT-HOVER",
                "/qen eval quest select $questID inner select $innerID reward select {rewardID} editor reward in edit page 0"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorFinishReward(questID: String, innerID: String, rewardID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val finish = inner.reward.getFinishScript(rewardID)
        EditorOfList(this, asLangText("EDITOR-EDIT-FINISH_REWARD", questID, innerID, rewardID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .list(page, 3, finish, true, "EDITOR-EDIT-FINISH_REWARD-LIST",
                "qen eval quest select $questID inner select $innerID innerReward select $rewardID editor reward in edit page {0}",
                EditorListModule.EditorButton("EDITOR-SCRIPT-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID innerReward select $rewardID editor reward in del {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorFailReward(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-EDIT-FAIL_REWARD", questID, innerID))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID editor inner in edit home")
            .list(page, 3, inner.reward.fail, true, "EDITOR-EDIT-FAIL_REWARD-LIST",
                "qen eval quest select $questID inner select $innerID editor fail in list page {page}",
                EditorListModule.EditorButton("EDITOR-SCRIPT-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID editor fail in del {index}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.selectTargetList(questID: String, innerID: String, targetID: String, page: Int = 0) {
        EditorSelTarget(this, asLangText("EDITOR-SELECT-TARGET", questID, innerID, targetID))
            .editorBack(this,
                "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in edit home")
            .list(page, 7, RegisterTarget.saveTarget.map { it.key }, true, "EDITOR-SELECT",
                "qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in sel list page {page}",
                EditorListModule.EditorButton("EDITOR-SELECT-TARGET-SEL",
                    "EDITOR-SELECT-TARGET-SEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in change name to {targetName}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.selectReward(questID: String, innerID: String, targetID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-SELECT-REWARD", questID, innerID))
            .editorBack(this,
                "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in edit home")
            .list(page, 7, inner.reward.finish, true, "EDITOR-SELECT-REWARD-LIST",
                "qen eval editor target in sel reward page {page}",
                EditorListModule.EditorButton("EDITOR-SELECT-REWARD-SEL",
                    "EDITOR-SELECT-REWARD-SEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID innerTarget select $targetID editor target in edit reward_boolean"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorNodeList(questID: String, innerID: String, target: QuestTarget, node: String, page: Int = 0) {
        val list = target.nodeMeta(node)?: mutableListOf()
        val id = target.id
        EditorOfList(this, asLangText("EDITOR-TARGET-LIST-UI-NODE",
            questID, innerID, id, asLangText("EDITOR-TARGET-LIST-NODE-${node.uppercase()}")))
            .editorBack(this, "/qen eval quest select $questID inner select $innerID innerTarget select $id editor target in edit home")
            .list(page, 7, list, true, "EDITOR-TARGET-LIST-FOR-NODE",
                "/qen eval quest select $questID inner select $innerID innerTarget select $id editor target in sel node page {page} to '$node'",
                EditorListModule.EditorButton("EDITOR-CONDITION-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL-META", "EDITOR-LIST-DEL-HOVER",
                    "/qen eval quest select $questID inner select $innerID innerTarget select $id editor target in change node to '$node' {index}"))
            .json.sendTo(adaptPlayer(this))
    }
}