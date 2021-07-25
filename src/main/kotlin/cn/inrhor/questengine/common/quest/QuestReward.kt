package cn.inrhor.questengine.common.quest

class QuestReward(val questID: String, val innerQuestID: String,
                  var finishReward: MutableMap<String, MutableList<String>>,
                  var failReward: MutableList<String>) {
}