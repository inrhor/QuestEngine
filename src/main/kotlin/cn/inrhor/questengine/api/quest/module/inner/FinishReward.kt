package cn.inrhor.questengine.api.quest.module.inner

class FinishReward(val id: String, val script: List<String>) {
    constructor(): this("nullRewardID", listOf())
}