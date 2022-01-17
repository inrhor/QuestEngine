package cn.inrhor.questengine.api.quest.module.inner

import taboolib.library.configuration.PreserveNotNull

@PreserveNotNull
class QuestReward(
    var finishReward: List<FinishReward>,
    var failReward: List<String>) {

    constructor(): this(listOf(), listOf())

    fun getFinishReward(rewardID: String): List<String> {
        finishReward.forEach {
            if (it.rewardID == rewardID) return it.script
        }
        return listOf()
    }

}