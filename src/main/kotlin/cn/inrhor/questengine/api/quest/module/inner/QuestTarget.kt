package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.utlis.variableReader
import taboolib.common.platform.function.info

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
            val sp = it.split("\n")
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
        if (nodeMap.containsKey(meta)) {
            val list = nodeMap[meta]!!
            list.forEach {
                info("eee '$it'")
            }
            list.remove("")
            return list
        }
        return null
    }
}
