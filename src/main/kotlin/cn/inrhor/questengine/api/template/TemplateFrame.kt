package cn.inrhor.questengine.api.template

import cn.inrhor.questengine.utlis.replaceWithOrder

class TemplateFrame(
    val type: TemplateType = TemplateType.CHAT_DIALOG,
    val header: List<String> = listOf(),
    val addIndex: Int = 0,
    val add: String = "",
    val footer: List<String> = listOf()) {

    fun replyList(strList: List<String>): List<String> {
        val list = mutableListOf<String>()
        header.forEach {
            val s = it.replaceWithOrder(*strList.toTypedArray())
            list.add(s)
        }
        if (add.isNotEmpty()) {
            val addList = mutableListOf<String>()
            for (i in addIndex until strList.size) {
                addList.add(add.replace("__variable__", "<$i>", true))
            }
            addList.forEach {
                val s = it.replaceWithOrder(*strList.toTypedArray())
                list.add(s)
            }
        }
        footer.forEach {
            list.add(it)
        }
        return list
    }

}

enum class TemplateType {
    CHAT_DIALOG
}