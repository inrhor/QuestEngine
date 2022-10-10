package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.utlis.variableReader
import taboolib.common.util.VariableReader

data class TargetFrame(
    var id: String, var event: String,
    var period: Int, var async: Boolean, var condition: String,
    var node: String, val description: List<String>, val data: List<String>
) {
    constructor():
            this("targetId", "targetName", 0, false,
                "", "", listOf(), listOf())

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

    fun nodeMeta(meta: String): MutableList<String>? {
        if (nodeMap.containsKey(meta)) {
            return nodeMap[meta]!!
        }
        return null
    }
}
