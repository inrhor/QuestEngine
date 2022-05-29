package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.utlis.variableReader

class QuestTarget(var id: String, var name: String, var reward: String,
                  var period: Int, var async: Boolean, var condition: String,
                  var node: String, val ui: UiFrame
) {
    constructor():
            this("targetId", "targetName", "", 0, false, "", "", UiFrame())

    @Transient
    val nodeMap: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun loadNode() {
        node.variableReader().forEach {
            if (it.isEmpty()) return@forEach
            val sp = it.split(" ")
            val l = sp.toMutableList()
            l.removeAt(0)
            nodeMap[sp[0]] = l
        }
    }

    fun reloadNode(newNode: String, newList: MutableList<String>) {
        node = ""
        nodeMap.remove(newNode)
        nodeMap.forEach { (t, u) ->
            node +="{{$t ${u.joinToString(" ")}}}"
        }
        nodeMap[newNode] = newList
        node +="{{$newNode ${newList.joinToString(" ")}}}"
    }

    fun nodeMeta(meta: String): MutableList<String>? {
        if (nodeMap.containsKey(meta)) return nodeMap[meta]
        return null
    }
}
