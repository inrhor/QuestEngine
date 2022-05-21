package cn.inrhor.questengine.common.editor

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
                    "/qen eval editor quest in edit home select {questID}"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-QUEST-DEL-META",
                    "EDITOR-LIST-QUEST-DEL-HOVER",
                    "/qen eval editor quest in del select {questID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorListInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-LIST-INNER", questID))
            .editorBack(this, "/qen eval editor quest in edit home select $questID")
            .add(asLangText("EDITOR-LIST-INNER-ADD"),
            EditorListModule.EditorButton(asLangText("EDITOR-LIST-INNER-ADD-META"),
                asLangText("EDITOR-LIST-INNER-ADD-HOVER"),
            "/qen eval editor inner in add select $questID"))
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-LIST-INNER-INFO",
                "qen eval editor quest in list page {page}",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-EDIT-META",
                    "EDITOR-LIST-INNER-EDIT-HOVER",
                    "/qen eval editor inner in edit home select $questID {innerID}"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-DEL-META",
                    "EDITOR-LIST-INNER-DEL-HOVER",
                    "/qen eval editor inner in del select $questID {innerID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorStartInner(questID: String, page: Int = 0) {
        val questModule = QuestManager.getQuestModule(questID)?: return
        EditorInnerList(this, questModule, asLangText("EDITOR-EDIT-QUEST-INNER-START", questID))
            .editorBack(this, "/qen eval editor quest in edit home select $questID")
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen eval editor quest in edit page {page} select $questID",
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE"),
                EditorListModule.EditorButton("EDITOR-EDIT-QUEST-START-STATE-META",
                    "EDITOR-EDIT-QUEST-START-STATE-HOVER",
                    "/qen eval editor quest in change start to {innerID} select $questID"))
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
            .editorBack(player, "/qen eval editor quest in edit home select $questID")
            .list(page, 3, list, true, "EDITOR-$meta-LIST",
                "qen eval editor quest in edit $cmd page {page} select $questID",
                EditorListModule.EditorButton("EDITOR-$meta-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-META", "EDITOR-LIST-HOVER",
                    "/qen eval editor quest in change $cmd to {index} select $questID"))
            .json.sendTo(adaptPlayer(player))
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
            .editorBack(this, "/qen eval editor inner in edit home select $questID $innerID")
            .list(page, 7, questModule.innerQuestList, true, "EDITOR-EDIT-INNER-LIST",
                "qen eval editor inner in edit nextinner page {page} select $questID $innerID",
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-CHOOSE"),
                EditorListModule.EditorButton("EDITOR-EDIT-INNER-NEXT-META",
                    "EDITOR-EDIT-INNER-NEXT-HOVER",
                    "/qen eval editor inner in change nextinner to {innerID} $questID $innerID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorInnerDesc(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-EDIT-INNER-NOTE", questID, innerID), empty = "  ")
            .editorBack(this, "/qen eval editor inner in edit home select $questID $innerID")
            .add(asLangText("EDITOR-LIST-INNER-DESC-ADD"),
                EditorListModule.EditorButton(asLangText("EDITOR-LIST-INNER-DESC-ADD-META"),
                    asLangText("EDITOR-LIST-INNER-DESC-ADD-HOVER"),
                    "/qen eval editor inner in change desc add to {head} select $questID $innerID"))
            .list(page, 5, inner.description, true, "EDITOR-LIST-INNER-NOTE-LIST",
                "qen eval editor inner in edit desc page {page} select $questID $innerID",
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-ADD-META",
                    "EDITOR-LIST-INNER-NOTE-ADD-HOVER",
                    "/qen eval editor inner in change desc add to {index} select $questID $innerID"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-INNER-NOTE-DEL-META",
                    "EDITOR-LIST-INNER-NOTE-DEL-HOVER",
                    "/qen eval editor inner in change desc del to {index} select $questID $innerID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorTargetList(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorTargetList(this, asLangText("EDITOR-TARGET", questID, innerID))
            .list(page, 7, inner.target, true,
                "EDITOR-TARGET-LIST",
                "qen eval editor target in list page {page} select $questID $innerID",
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT"),
            EditorListModule.EditorButton("EDITOR-TARGET-EDIT-META",
                "EDITOR-TARGET-EDIT-HOVER",
                "/qen eval target in edit home select $questID $innerID {targetID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorRewardList(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorRewardList(this, asLangText("EDITOR-FINISH_REWARD", questID, innerID))
            .editorBack(this, "/qen eval editor inner in edit home select $questID $innerID")
            .list(page, 7, inner.reward.finish, true, "EDITOR-FINISH_REWARD-LIST",
            "qen eval editor reward in list page {page} select $questID $innerID",
            EditorListModule.EditorButton("EDITOR-EDIT-FINISH_REWARD-EDIT"),
            EditorListModule.EditorButton("EDITOR-EDIT-FINISH_REWARD-EDIT-META",
            "EDITOR-EDIT-FINISH_REWARD-EDIT-HOVER",
                "/qen eval editor reward in edit page 0 select $questID $innerID {rewardID}"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorFinishReward(questID: String, innerID: String, rewardID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        val finish = inner.reward.getFinishReward(rewardID)
        EditorOfList(this, asLangText("EDITOR-EDIT-FINISH_REWARD", questID, innerID, rewardID))
            .editorBack(this, "/qen eval editor inner in edit home select $questID $innerID")
            .list(page, 3, finish, true, "EDITOR-EDIT-FINISH_REWARD-LIST",
                "qen eval editor reward in edit page {0} select $questID $innerID $rewardID",
                EditorListModule.EditorButton("EDITOR-SCRIPT-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-META", "EDITOR-LIST-HOVER",
                    "/qen eval editor reward in del {index} select $questID $innerID $rewardID"))
            .json.sendTo(adaptPlayer(this))
    }

    fun Player.editorFailReward(questID: String, innerID: String, page: Int = 0) {
        val inner = QuestManager.getInnerQuestModule(questID, innerID)?: return
        EditorOfList(this, asLangText("EDITOR-EDIT-FAIL_REWARD", questID, innerID))
            .editorBack(this, "/qen eval editor inner in edit home select $questID $innerID")
            .list(page, 3, inner.reward.fail, true, "EDITOR-EDIT-FAIL_REWARD-LIST",
                "qen eval editor fail in list page {page} select $questID $innerID",
                EditorListModule.EditorButton("EDITOR-SCRIPT-RETURN"),
                EditorListModule.EditorButton("EDITOR-LIST-DEL"),
                EditorListModule.EditorButton("EDITOR-LIST-META", "EDITOR-LIST-HOVER",
                    "/qen eval editor fail in del {index} select $questID $innerID"))
            .json.sendTo(adaptPlayer(this))
    }
}