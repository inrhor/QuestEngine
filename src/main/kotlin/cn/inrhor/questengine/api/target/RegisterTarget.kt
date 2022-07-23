package cn.inrhor.questengine.api.target

import cn.inrhor.questengine.common.quest.target.node.BlockNode
import cn.inrhor.questengine.common.quest.target.node.CauseNode
import cn.inrhor.questengine.common.quest.target.node.IdNode
import cn.inrhor.questengine.script.kether.runEval
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object RegisterTarget {

    @Awake(LifeCycle.LOAD)
    fun loadNode() {
        val block = BlockNode()
        val amount = TargetNode("amount", TargetNodeType.INT)
        val number = TargetNode("number", TargetNodeType.INT)
        val id = IdNode()
        val need = TargetNode("need", TargetNodeType.LIST)
        add("break block", block, amount)
        add("place block", block, amount)
        add("enchant item", TargetNode("cost", TargetNodeType.DOUBLE), number)
        add("player kill entity", number, TargetNode("entity", TargetNodeType.STRING),
            TargetNode("check", TargetNodeType.INT),
            TargetNode("condition", TargetNodeType.LIST))
        add("left npc", id, need)
        add("right npc", id, need)
        add("left ady", id, need)
        add("right ady", id, need)
        add("player chat", TargetNode("message", TargetNodeType.STRING), number)
        add("player send command", TargetNode("content", TargetNodeType.STRING), number)
        add("player death", CauseNode(), number)
        add("player join server", number)
        add("player quit server", number)
        add("player fish", TargetNode("entitylist"), TargetNode("hook"), TargetNode("state"), TargetNode("exp", TargetNodeType.INT), amount)
        add("TASK")
    }

    fun add(name: String, vararg targetNode: TargetNode) {
        if (targetNode.isEmpty()) saveTarget[name] = mutableListOf()
        targetNode.forEach {
            if (saveTarget.containsKey(name)) {
                saveTarget[name]!!.add(it)
            }else {
                saveTarget[name] = mutableListOf(it)
            }
        }
    }

    fun getNodeList(name: String): MutableList<TargetNode> {
        return saveTarget[name]?: mutableListOf()
    }

    fun getNode(name: String, node: String): TargetNode? {
        getNodeList(name).forEach {
            if (it.node == node) return it
        }
        return null
    }

    val saveTarget: MutableMap<String, MutableList<TargetNode>> = mutableMapOf()

}

enum class TargetNodeType {
    STRING,INT,DOUBLE,BOOLEAN,LIST
}

open class TargetNode(val node: String, val nodeType: TargetNodeType = TargetNodeType.LIST) {

    open fun contains(content: String, player: Player): Boolean {
        return runEval(player, content)
    }

}