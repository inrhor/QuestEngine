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
        val adyId = IdNode()
        val npcId = IdNode()
        val mobs = TargetNode("mobs")
        val need = TargetNode("need")
        val item =TargetNode("need", TargetNodeType.STRING)
        add("break block", block, amount)
        add("place block", block, amount)
        add("enchant item", TargetNode("cost", TargetNodeType.DOUBLE), number, item)
        add("player kill entity", number, TargetNode("entity", TargetNodeType.STRING),
            TargetNode("check", TargetNodeType.INT),
            TargetNode("condition", TargetNodeType.LIST))
        add("left npc", npcId, need)
        add("right npc", npcId, need)
        add("left ady", adyId, need)
        add("right ady", adyId, need)
        add("player kill mythicmobs", mobs, number)
        add("player chat", TargetNode("message", TargetNodeType.STRING), number)
        add("player send command", TargetNode("content", TargetNodeType.STRING), number)
        add("player death", CauseNode(), number)
        add("player join server", number)
        add("player quit server", number)
        add("player fish", TargetNode("entitylist"), TargetNode("hook"), TargetNode("state"), TargetNode("exp", TargetNodeType.INT), amount)
        add("craft item", item, amount, TargetNode("matrix"))
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