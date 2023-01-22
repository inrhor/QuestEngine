package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.utlis.variableReader
import taboolib.common.util.VariableReader

data class TargetFrame(
    var id: String = "null", var event: String = "null",
    var period: Int = 0, var async: Boolean = false, var condition: String = "",
    var node: String = "", val description: List<String> = listOf(),
    val data: List<String> = listOf(),
    val trigger: MutableList<ControlFrame> = mutableListOf()
) {

    @Transient
    val nodeMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun loadNode() {
        // {{}}
        node.variableReader().forEach {
            if (it.isEmpty()) return@forEach
            // <>
            val meta = VariableReader("<", ">").readToFlatten(it)[0]
            if (!meta.isVariable) return@forEach
            val list = mutableListOf<String>()
            val content = VariableReader("[", "]").readToFlatten(it)
            content.forEach { c ->
                if (c.isVariable) list.add(c.text)
            }
            nodeMap[meta.text] = list
        }
    }

    fun reloadNode(newNode: String, newList: MutableList<String>) {
        node = ""
        nodeMap.remove(newNode)
        nodeMap.forEach { (t, u) ->
            node +="{{<$t>\n[$u]\n}}"
        }
        nodeMap[newNode] = newList
        if (newList.isEmpty()) return
        node +="{{<$newNode>"
        newList.forEach {
            node += "[$it]"
        }
        node += "}}"
    }

    fun nodeMeta(meta: String): MutableList<String> {
        if (nodeMap.containsKey(meta)) {
            return nodeMap[meta]!!
        }
        return mutableListOf()
    }
}
