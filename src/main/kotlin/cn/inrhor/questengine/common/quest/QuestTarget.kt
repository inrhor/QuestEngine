package cn.inrhor.questengine.common.quest

import taboolib.library.configuration.YamlConfiguration

class QuestTarget(val name: String, var time: String, val reward: String,
                  var period: Int, var async: Boolean, var conditions: MutableList<String>,
                  val condition: MutableMap<String, String>, val conditionList: MutableMap<String, MutableList<String>>,
                  var yaml: YamlConfiguration
) {
    constructor(name: String, time: String, reward: String,
                period: Int, async: Boolean, conditions: MutableList<String>,
                condition: MutableMap<String, String>, yaml: YamlConfiguration):
            this(name, time, reward, period, async, conditions, condition, mutableMapOf(), yaml)
}