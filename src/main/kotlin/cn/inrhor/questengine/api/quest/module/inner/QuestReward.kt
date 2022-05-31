package cn.inrhor.questengine.api.quest.module.inner

class QuestReward(
    var finish: MutableList<FinishReward>,
    var fail: String) {

    constructor(): this(mutableListOf(), "")

    fun existRewardID(rewardID: String): Boolean {
        finish.forEach {
            if (it.id == rewardID) return true
        }
        return false
    }

    fun getFinishScript(rewardID: String): String {
        finish.forEach {
            if (it.id == rewardID) return it.script
        }
        return ""
    }

    fun getFinishReward(rewardID: String): FinishReward? {
        finish.forEach {
            if (it.id == rewardID) return it
        }
        return null
    }

}