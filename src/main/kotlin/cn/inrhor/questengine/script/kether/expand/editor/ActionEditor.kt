package cn.inrhor.questengine.script.kether.expand.editor

import taboolib.module.kether.*

class ActionEditor {

    enum class QuestUi {
        HOME, LIST, ADD, DEL, EDIT, CHANGE
    }

    enum class TargetUi {
        LIST,EDIT,ADD,DEL,SEL,CHANGE
    }

    enum class ListUi {
        LIST, DEL, ADD
    }

    enum class TimeUi {
        EDIT,CHANGE
    }

    companion object {
        @KetherParser(["editor"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                /**
                 * editor quest in home
                 * editor quest in list page [page]
                 * editor quest in add/del
                 * editor quest in edit [meta]
                 * editor quest in edit [meta] page [page]
                 * editor quest in change [meta] to [change] add/del
                 */
                case("quest") {
                    it.mark()
                    it.expect("in")
                    when (val ui = QuestUi.valueOf(it.nextToken().uppercase())) {
                        QuestUi.LIST -> {
                            it.expect("page")
                            EditorQuest(ui, page = it.nextInt())
                        }
                        QuestUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "note", "acceptcondition", "groupnote" -> {
                                    it.expect("page")
                                    EditorQuest(ui, meta, page = it.nextInt())
                                }
                                else -> {
                                    EditorQuest(ui, meta)
                                }
                            }
                        }
                        QuestUi.CHANGE -> {
                            val meta = it.nextToken()
                            it.expect("to")
                            when (meta) {
                                "note", "acceptcondition", "groupnote" -> {
                                    EditorQuest(ui, meta, it.nextToken(), it.nextToken())
                                }
                                else -> EditorQuest(ui, meta, it.nextToken())
                            }
                        }
                        else -> EditorQuest(ui)
                    }
                }
                /**
                 * editor target in list page [page]
                 * editor target in edit [meta]
                 */
                case("target") {
                    it.mark()
                    it.expect("in")
                    when (val ui = TargetUi.valueOf(it.nextToken().uppercase())) {
                        TargetUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            EditorTarget(ui, page = page)
                        }
                        TargetUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "condition" -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    EditorTarget(ui, meta, page = page)
                                }
                                "node" -> {
                                    it.expect("to")
                                    EditorTarget(ui, meta, it.nextToken())
                                }
                                else -> {
                                    EditorTarget(ui, meta)
                                }
                            }
                        }
                        TargetUi.ADD, TargetUi.DEL -> {
                            EditorTarget(ui)
                        }
                        TargetUi.SEL -> {
                            when (val meta = it.nextToken()) {
                                "node" -> {
                                    it.expect("to")
                                    val o = it.nextToken()
                                    it.expect("page")
                                    val page = it.nextInt()
                                    EditorTarget(ui, meta, o, page = page)
                                }
                                else -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    EditorTarget(ui, meta, page = page)
                                }
                            }
                        }
                        TargetUi.CHANGE -> {
                            val meta = it.nextToken()
                            it.expect("to")
                            val change = it.nextToken()
                            when (meta) {
                                "node" -> {
                                    EditorTarget(ui, meta, change, it.nextToken(), it.nextToken())
                                }
                                "condition" -> {
                                    EditorTarget(ui, meta, change, it.nextToken())
                                }
                                else -> {
                                    EditorTarget(ui, meta, change)
                                }
                            }
                        }
                        else -> error("unknown ui")
                    }
                }
                case("dialog") {
                    EditorDialog()
                }
                case("time") {
                    it.mark()
                    it.expect("in")
                    when (val ui = TimeUi.valueOf(it.nextToken().uppercase())) {
                        TimeUi.EDIT -> {
                            val meta = it.nextToken()
                            EditorTime(ui, meta)
                        }
                        TimeUi.CHANGE -> {
                            val meta = it.nextToken()
                            it.expect("to")
                            val change = it.nextToken()
                            EditorTime(ui, meta, change)
                        }
                    }
                }
            }
        }
    }
}