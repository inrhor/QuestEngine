package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.utlis.variableReader
import taboolib.common.platform.function.info

data class TargetFrame(var id: String, var event: String,
                       var period: Int, var async: Boolean, var condition: String,
                       var node: String, val ui: UiFrame
) {
    constructor():
            this("targetId", "targetName", 0, false, "", "", UiFrame())

    @Transient
    val nodeMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun loadNode() {
        node.variableReader().forEach {
            if (it.isEmpty()) return@forEach
            val sp = it.split("\n", " ")
            val l = sp.toMutableList()
            l.removeAt(0)
            nodeMap[sp[0]] = l
        }
    }

    fun reloadNode(newNode: String, newList: MutableList<String>) {
        node = ""
        nodeMap.remove(newNode)
        nodeMap.forEach { (t, u) ->
            node +="{{$t\n${u.joinToString("\n")}}}"
        }
        nodeMap[newNode] = newList
        node +="{{$newNode\n${newList.joinToString("\n")}}}"
    }

    fun nodeMeta(meta: String): MutableList<String>? {
        nodeMap.keys.forEach { info("nodeKey $it") }
        if (nodeMap.containsKey(meta)) {
            val list = nodeMap[meta]!!
            list.remove("")
            return list
        }
        return null
    }
}
