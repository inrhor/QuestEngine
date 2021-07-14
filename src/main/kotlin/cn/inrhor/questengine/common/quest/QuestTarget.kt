package cn.inrhor.questengine.common.quest

class QuestTarget(val name: String, var time: String, val reward: String, val condition: MutableMap<String, String>, val conditionList: MutableMap<String, MutableList<String>>) {
    constructor(name: String, time: String, reward: String, condition: MutableMap<String, String>):
            this(name, time, reward, condition, mutableMapOf())
}