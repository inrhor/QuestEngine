package cn.inrhor.questengine.api.quest

class ControlLogModule(
    var highestLogEnable: Boolean,
    var normalLogEnable: Boolean,
    var highestLogType: String,
    var normalLogType: String,
    var highestReKether: MutableList<String>,
    var normalReKether: MutableList<String>) {

    fun returnHighestReKether(questID: String, innerID: String, priority: String): MutableList<String> {
        return replaceList(highestReKether, questID, innerID, priority)
    }

    fun returnNormalReKether(questID: String, innerID: String, priority: String): MutableList<String> {
        return replaceList(normalReKether, questID, innerID, priority)
    }

    fun replaceList(list: MutableList<String>, questID: String, innerID: String, priority: String): MutableList<String> {
        list.forEach {
            list.add(it.replace("@this", "$questID $innerID $priority", true))
        }
        return list
    }

}