package cn.inrhor.questengine.common.quest

class QuestReward(val questID: String, val mainQuestID: String, val subQuestID: String,
                  var finishReward: MutableMap<String, MutableList<String>>,
                  var failReward: MutableList<String>) {
}