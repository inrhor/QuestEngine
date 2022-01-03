package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.utlis.ui.BuilderFrame

class QuestTarget(val name: String, var time: String, val reward: String,
                  var period: Int, var async: Boolean, var conditions: List<String>,
                  val condition: MutableMap<String, String>, val conditionList: MutableMap<String, List<String>>,
                  val ui: BuilderFrame
) {
    constructor(name: String, time: String, reward: String,
                period: Int, async: Boolean, conditions: List<String>,
                condition: MutableMap<String, String>, ui: BuilderFrame):
            this(name, time, reward, period, async, conditions, condition, mutableMapOf(), ui)
}