package cn.inrhor.questengine.api.quest.control

/**
 * 控制记录模块
 */
class ControlLogType(var isEnable: Boolean, var logType: String, var reKether: MutableList<String>) {

    fun returnReKether(questID: String, innerID: String, priority: String): MutableList<String> {
        return replaceList(reKether, questID, innerID, priority)
    }

    fun replaceList(list: MutableList<String>, questID: String, innerID: String, priority: String): MutableList<String> {
        list.forEach {
            list.add(it.replace("@this", "$questID $innerID $priority", true))
        }
        return list
    }

}