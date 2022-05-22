package cn.inrhor.questengine.api.target

import cn.inrhor.questengine.common.quest.target.TBreakBlock
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object RegisterTarget {

    @Awake(LifeCycle.LOAD)
    fun loadNode() {
        add(
            TBreakBlock.name,
            TargetNode("block", TargetNodeType.LIST),
            TargetNode("amount", TargetNodeType.INT)
        )
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
    STRING,INT,BOOLEAN,LIST
}

class TargetNode(node: String, nodeType: TargetNodeType)