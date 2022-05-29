package cn.inrhor.questengine.script.kether.expand.editor

import taboolib.module.kether.*

class ActionEditor {

    enum class QuestUi {
        HOME, LIST, ADD, DEL, EDIT, CHANGE
    }

    enum class InnerUi {
        LIST, DEL, EDIT, CHANGE, ADD
    }

    enum class TargetUi {
        LIST,EDIT,ADD,DEL,SEL,CHANGE
    }

    enum class RewardUi {
        LIST, EDIT, DEL
    }

    enum class ListUi {
        LIST, DEL
    }

    enum class TimeUi {
        EDIT,CHANGE
    }

    companion object {
        @KetherParser(["editor"], namespace = "QuestEngine", shared = true)
        fun parser() = scriptParser {
            it.switch {
                /**
                 * editor quest in home
                 * editor quest in list page [page]
                 * editor quest in add/del
                 * editor quest in edit [meta]
                 * editor quest in edit [meta] page [page]
                 * editor quest in change [meta] to [change]
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
                                "acceptcondition", "failurecondition", "failurescript" -> {
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
                            EditorQuest(ui, meta, it.nextToken())
                        }
                        else -> {
                            EditorQuest(ui)
                        }
                    }
                }
                /**
                 * editor inner in list page [page]
                 * editor inner in add
                 * editor inner in del
                 * editor inner in edit [meta]
                 * editor inner in edit [meta] page [page]
                 * editor inner in change [meta] (desc) to [change]
                 */
                case("inner") {
                    it.mark()
                    it.expect("in")
                    when (val ui = InnerUi.valueOf(it.nextToken().uppercase())) {
                        InnerUi.LIST -> {
                            it.expect("page")
                            EditorInner(ui, page = it.nextInt())
                        }
                        InnerUi.ADD, InnerUi.DEL -> {
                            EditorInner(ui)
                        }
                        InnerUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "nextinner" -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    EditorInner(ui, meta, page = page)
                                }
                                else -> {
                                    EditorInner(ui, meta)
                                }
                            }
                        }
                        InnerUi.CHANGE -> {
                            when (val meta = it.nextToken()) {
                                "desc" -> {
                                    val tag = it.nextToken()
                                    it.expect("to")
                                    val change = it.nextToken()
                                    EditorInner(ui, meta, tag, change)
                                }
                                else -> {
                                    it.expect("to")
                                    val change = it.nextToken()
                                    EditorInner(ui, meta, change)
                                }
                            }
                        }
                        else -> error("unknown ui")
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
                                    val to = it.nextToken()
                                    EditorTarget(ui, meta, to)
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
                                    val index = it.nextToken()
                                    EditorTarget(ui, meta, change, index)
                                }
                                else -> {
                                    EditorTarget(ui, meta, change)
                                }
                            }
                        }
                        else -> error("unknown ui")
                    }
                }
                /**
                 * editor reward in list page [page]
                 * editor reward in edit page [page]
                 * editor reward in del [index]
                 */
                case("reward") {
                    it.mark()
                    it.expect("in")
                    when (val ui = RewardUi.valueOf(it.nextToken().uppercase())) {
                        RewardUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            EditorReward(ui, page = page)
                        }
                        RewardUi.EDIT -> {
                            it.expect("page")
                            val page = it.nextInt()
                            EditorReward(ui, page = page)
                        }
                        RewardUi.DEL -> {
                            val index = it.nextInt()
                            EditorReward(ui, index = index)
                        }
                        else -> error("unknown ui")
                    }
                }
                /**
                 * editor fail in list page [page]
                 * editor fail in del [index]
                 */
                case("fail") {
                    it.mark()
                    it.expect("in")
                    when (val ui = ListUi.valueOf(it.nextToken().uppercase())) {
                        ListUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            EditorInnerFail(ui, page=page)
                        }
                        ListUi.DEL -> {
                            val index = it.nextToken()
                            EditorInnerFail(ui, index)
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