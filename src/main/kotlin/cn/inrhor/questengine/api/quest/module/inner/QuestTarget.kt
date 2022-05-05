package cn.inrhor.questengine.api.quest.module.inner

import cn.inrhor.questengine.utlis.ui.BuilderFrame


class QuestTarget(val id: String, val name: String, var time: String, val reward: String,
                  var period: Int, var async: Boolean, var conditions: List<String>,
                  val node: String, val ui: BuilderFrame
) {
    constructor():
            this("targetId", "targetName", "always", "", 0, false, listOf(), "", BuilderFrame())
}
