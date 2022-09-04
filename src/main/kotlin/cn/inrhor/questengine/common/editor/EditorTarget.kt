package cn.inrhor.questengine.common.editor

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.RegisterTarget
import cn.inrhor.questengine.api.target.TargetNode
import cn.inrhor.questengine.api.target.TargetNodeType
import cn.inrhor.questengine.common.editor.EditorList.editorNodeList
import cn.inrhor.questengine.common.quest.manager.QuestManager.getQuestFrame
import cn.inrhor.questengine.common.quest.manager.QuestManager.getTargetFrame
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.util.setSafely
import taboolib.module.chat.TellrawJson
import taboolib.module.nms.inputSign
import taboolib.platform.util.asLangText

object EditorTarget {

    val editMeta = mutableListOf(
        "NAME", "ASYNC", "CONDITION")

   fun Player.editorTarget(questID: String, targetID: String) {
        val target = targetID.getTargetFrame(questID)
        val json = TellrawJson()
            .newLine()
            .append("   "+asLangText("EDITOR-EDIT-TARGET", questID, targetID))
            .newLine()
            .append("      "+asLangText("EDITOR-BACK"))
            .append("  "+asLangText("EDITOR-BACK-META"))
            .hoverText(asLangText("EDITOR-BACK-HOVER"))
            .runCommand("/qen eval quest select $questID target select $targetID editor target in list page 0")
            .newLine()
            .newLine()
       if (target.event.uppercase().contains("TASK ")) editMeta.add("PERIOD")
       editMeta.forEach {
           val t = "${target.async}".uppercase()
           json.append("      " + asLangText("EDITOR-EDIT-TARGET-$it",
               target.event, target.period, asLangText("ASYNC-BOOLEAN-META-$t")))
               .append("  " + asLangText("EDITOR-EDIT-TARGET-META"))
           if (it == "ASYNC") {
               json.hoverText(asLangText("EDITOR-EDIT-TARGET-BOOLEAN-META-HOVER"))
           }else {
               json.hoverText(asLangText("EDITOR-EDIT-TARGET-META-HOVER"))
           }
           when (it) {
               "CONDITION" -> {
                   json.runCommand("/qen eval quest select $questID target select $targetID editor target in edit "+it.lowercase()+" page 0").newLine()
               }
               else -> {
                   json.runCommand("/qen eval quest select $questID target select $targetID editor target in edit "+it.lowercase()).newLine()
               }
           }
       }
       RegisterTarget.getNodeList(target.event).forEach {
           val node = it.node
           if (it.nodeType == TargetNodeType.LIST) {
               json.append("      "+asLangText("EDITOR-TARGET-LIST-NODE",
                   asLangText("EDITOR-TARGET-LIST-NODE-${node.uppercase()}")))
                   .append("  "+asLangText("EDITOR-TARGET-NODE-META"))
                   .hoverText(asLangText("EDITOR-TARGET-NODE-META-HOVER"))
                   .runCommand("/qen eval quest select $questID target select $targetID editor target in sel node to '$node' page 0")
                   .newLine()
           }else {
               val nodeMeta = target.nodeMeta(node)
               json.append("      "+asLangText("EDITOR-TARGET-NODE",
                   asLangText("EDITOR-TARGET-NODE-${node.uppercase()}"), nodeMeta?.get(0)?: "NULL"))
                   .append("  "+asLangText("EDITOR-TARGET-NODE-META"))
               if (it.nodeType == TargetNodeType.BOOLEAN) {
                   json.hoverText(asLangText("EDITOR-TARGET-NODE-BOOLEAN-HOVER"))
               }else json.hoverText(asLangText("EDITOR-TARGET-NODE-META-HOVER"))
               json.runCommand("/qen eval quest select $questID target select $targetID editor target in edit node to '$node'").newLine()
           }
       }
        json.newLine().sendTo(adaptPlayer(this))
    }

    fun Player.editorTargetNode(questID: String, target: TargetFrame, targetNode: TargetNode) {
        val node = targetNode.node
        when (targetNode.nodeType) {
            TargetNodeType.STRING -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-STRING"))) {
                    val meta = target.nodeMeta(node)?: mutableListOf()
                    meta.setSafely(0, it[2], "")
                    saveTarget(this, questID, target, node, meta)
                }
            }
            TargetNodeType.BOOLEAN -> {
                val meta = target.nodeMeta(node)?: mutableListOf()
                val b = meta[0].toBoolean()
                meta.setSafely(0, "${!b}", "FALSE")
                saveTarget(this, questID, target, node, meta)
            }
            TargetNodeType.INT -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-INT"))) {
                    val meta = target.nodeMeta(node)?: mutableListOf()
                    meta.setSafely(0, it[2], "0")
                    saveTarget(this, questID, target, node, meta)
                }
            }
            TargetNodeType.DOUBLE -> {
                inputSign(arrayOf(node, asLangText("EDITOR-TARGET-NODE-PLAYER-DOUBLE"))) {
                    val meta = target.nodeMeta(node)?: mutableListOf()
                    meta.setSafely(0, it[2], "0.0")
                    saveTarget(this, questID, target, node, meta)
                }
            }
            else -> {
                editorNodeList(questID, target, node)
            }
        }
    }

    private fun saveTarget(player: Player, questID: String, target: TargetFrame, node: String, list: MutableList<String>) {
        target.reloadNode(node, list)
        questID.getQuestFrame()
        player.editorTarget(questID, target.id)
    }

}