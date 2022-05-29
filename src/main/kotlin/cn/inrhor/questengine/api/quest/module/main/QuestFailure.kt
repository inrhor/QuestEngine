package cn.inrhor.questengine.api.quest.module.main

import cn.inrhor.questengine.utlis.removeAt


class QuestFailure(var check: Int, var condition: String, var script: String) {
    constructor(): this(-1, "", "")

    fun delCondition(int: Int) {
        condition = condition.removeAt(int)
    }

    fun delScript(int: Int) {
        script = script.removeAt(int)
    }
}