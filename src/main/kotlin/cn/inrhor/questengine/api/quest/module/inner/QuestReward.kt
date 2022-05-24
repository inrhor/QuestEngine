package cn.inrhor.questengine.api.quest.module.inner

class QuestReward(
    var finish: MutableList<FinishReward>,
    var fail: List<String>) {

    constructor(): this(mutableListOf(), listOf())

    fun getFinishScript(rewardID: String): List<String> {
        finish.forEach {
            if (it.id == rewardID) return it.script
        }
        return listOf()
    }

    fun getFinishReward(rewardID: String): FinishReward? {
        finish.forEach {
            if (it.id == rewardID) return it
        }
        return null
    }

}