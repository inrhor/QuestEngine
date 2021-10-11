package cn.inrhor.questengine.common.quest

import cn.inrhor.questengine.utlis.ui.NoteComponent

class QuestTarget(val name: String, var time: String, val reward: String,
                  var period: Int, var async: Boolean, var conditions: MutableList<String>,
                  val condition: MutableMap<String, String>, val conditionList: MutableMap<String, MutableList<String>>,
                  val noteMap: MutableMap<String, NoteComponent>
) {
    constructor(name: String, time: String, reward: String,
                period: Int, async: Boolean, conditions: MutableList<String>,
                condition: MutableMap<String, String>, noteMap: MutableMap<String, NoteComponent>):
            this(name, time, reward, period, async, conditions, condition, mutableMapOf(), noteMap)
}