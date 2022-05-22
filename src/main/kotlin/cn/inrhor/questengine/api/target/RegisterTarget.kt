package cn.inrhor.questengine.api.target

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object RegisterTarget {

    @Awake(LifeCycle.LOAD)
    fun loadNode() {
        val block = TargetNode("block", TargetNodeType.LIST)
        val amount = TargetNode("amount", TargetNodeType.INT)
        val number = TargetNode("number", TargetNodeType.INT)
        val id = TargetNode("id", TargetNodeType.LIST)
        val need = TargetNode("need", TargetNodeType.BOOLEAN)
        add("break block", block, amount)
        add("place block", block, amount)
        add("pass collection packet", number, TargetNode("packetID", TargetNodeType.STRING))
        add("enchant item", TargetNode("cost", TargetNodeType.DOUBLE), number)
        add("player kill entity", number, TargetNode("entity", TargetNodeType.STRING))
        add("left npc", id, need)
        add("right npc", id, need)
        add("player chat", TargetNode("message", TargetNodeType.STRING), number)
        add("player send command", TargetNode("content", TargetNodeType.STRING), number)
        add("player death", TargetNode("cause", TargetNodeType.LIST), number)
        add("player join server", number)
        add("player quit server", number)
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

    val saveTarget: MutableMap<String, MutableList<TargetNode>> = mutableMapOf()

}

enum class TargetNodeType {
    STRING,INT,DOUBLE,BOOLEAN,LIST
}

class TargetNode(node: String, nodeType: TargetNodeType)