package cn.inrhor.questengine.api.quest.module.inner

class QuestReward(
    var finish: MutableList<FinishReward>,
    var fail: List<String>) {

    constructor(): this(mutableListOf(), listOf())

    fun getFinishReward(rewardID: String): List<String> {
        finish.forEach {
            if (it.id == rewardID) return it.script
        }
        return listOf()
    }

}