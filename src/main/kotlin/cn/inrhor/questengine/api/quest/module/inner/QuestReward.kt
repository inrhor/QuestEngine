package cn.inrhor.questengine.api.quest.module.inner

class QuestReward(
    var questID: String, var innerQuestID: String,
    var finishReward: List<FinishReward>,
    var failReward: List<String>) {

    fun getFinishReward(rewardID: String): List<String> {
        finishReward.forEach {
            if (it.rewardID == rewardID) return it.script
        }
        return listOf()
    }

}