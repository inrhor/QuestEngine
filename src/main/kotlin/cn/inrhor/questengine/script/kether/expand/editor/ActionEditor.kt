package cn.inrhor.questengine.script.kether.expand.editor

import taboolib.module.kether.*

class ActionEditor {

    enum class QuestUi {
        HOME, LIST, ADD, DEL, EDIT, CHANGE
    }

    companion object {
        /**
         * editor quest in home
         * editor quest in list page [page]
         * editor quest in add/del select [questID]
         * editor quest in edit [meta] select [questID]
         * editor quest in change [meta] to [change] select [questID]
         */
        @KetherParser(["editor"], namespace = "QuestEngine", shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("quest") {
                    it.mark()
                    it.expect("in")
                    when (val ui = QuestUi.valueOf(it.nextToken().uppercase())) {
                        QuestUi.LIST -> {
                            it.expect("page")
                            EditorQuest(ui, page = it.nextInt())
                        }
                        QuestUi.ADD, QuestUi.DEL -> {
                            it.expect("select")
                            EditorQuest(ui, it.nextToken())
                        }
                        QuestUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "acceptcondition", "failurecondition", "failurescript" -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    it.expect("select")
                                    EditorQuest(ui, it.nextToken(), meta, page = page)
                                }
                                else -> {
                                    it.expect("select")
                                    EditorQuest(ui, it.nextToken(), meta)
                                }
                            }
                        }
                        QuestUi.CHANGE -> {
                            val meta = it.nextToken()
                            it.expect("to")
                            val change = it.nextToken()
                            it.expect("select")
                            EditorQuest(ui, it.nextToken(), meta, change)
                        }
                        else -> {
                            EditorQuest(ui)
                        }
                    }
                }
            }
        }
    }
}