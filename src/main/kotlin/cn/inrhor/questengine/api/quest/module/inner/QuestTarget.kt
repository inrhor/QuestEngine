package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.api.ui.UiFrame
import cn.inrhor.questengine.utlis.variableReader


class QuestTarget(val id: String, val name: String, var time: TimeModule, val reward: String,
                  var period: Int, var async: Boolean, var condition: List<String>,
                  val node: String, val ui: UiFrame
) {
    constructor():
            this("targetId", "targetName", TimeModule(), "", 0, false, listOf(), "", UiFrame())

    fun nodeMeta(meta: String): List<String>? {
        node.variableReader().forEach {
            val sp = it.split(" ")
            if (sp[0].uppercase() == meta.uppercase()) {
                val l = sp.toMutableList()
                l.removeAt(0)
                return l.toList()
            }
        }
        return null
    }
}
