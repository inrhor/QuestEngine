package cn.inrhor.questengine.common.quest.ui.book.handbook

import cn.inrhor.questengine.common.quest.ui.QuestSort
import cn.inrhor.questengine.utlis.ui.buildUI

object SortBook {

    fun home() {
        QuestSort.sortList.forEach { sort, qModule ->
            val ui = buildUI() {
                description.add("")
            }
        }
    }

}