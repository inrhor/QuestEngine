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
        LIST,EDIT
    }

    enum class RewardUi {
        LIST, EDIT, DEL
    }

    enum class ListUi {
        LIST, DEL
    }

    companion object {
        @KetherParser(["editor"], namespace = "QuestEngine", shared = true)
        fun parser() = scriptParser {
            it.switch {
                /**
                 * editor quest in home
                 * editor quest in list page [page]
                 * editor quest in add/del select [questID]
                 * editor quest in edit [meta] select [questID]
                 * editor quest in edit [meta] page [page] select [questID]
                 * editor quest in change [meta] to [change] select [questID]
                 */
                case("quest") {
                    it.mark()
                    it.expect("in")
                    when (val ui = QuestUi.valueOf(it.nextToken().uppercase())) {
                        QuestUi.LIST -> {
                            it.expect("page")
                            EditorQuest(ui, page = it.nextInt())
                        }
                        QuestUi.DEL -> {
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
                /**
                 * editor inner in list page [page] select [questID]
                 * editor inner in add select [questID]
                 * editor inner in del select [questID] [innerID]
                 * editor inner in edit [meta] select [questID] [innerID]
                 * editor inner in edit [meta] page [page] select [questID] [innerID]
                 * editor inner in change [meta] (desc) to [change] select [questID] [innerID]
                 */
                case("inner") {
                    it.mark()
                    it.expect("in")
                    when (val ui = InnerUi.valueOf(it.nextToken().uppercase())) {
                        InnerUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            it.expect("select")
                            EditorInner(ui, it.nextToken(), page = page)
                        }
                        InnerUi.ADD -> {
                            it.expect("select")
                            EditorInner(ui, it.nextToken())
                        }
                        InnerUi.DEL -> {
                            it.expect("select")
                            EditorInner(ui, it.nextToken(), it.nextToken())
                        }
                        InnerUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "nextinner" -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    it.expect("select")
                                    EditorInner(ui, it.nextToken(), it.nextToken(), meta, page = page)
                                }
                                else -> {
                                    it.expect("select")
                                    EditorInner(ui, it.nextToken(), it.nextToken(), meta)
                                }
                            }
                        }
                        InnerUi.CHANGE -> {
                            when (val meta = it.nextToken()) {
                                "desc" -> {
                                    val tag = it.nextToken()
                                    it.expect("to")
                                    val change = it.nextToken()
                                    it.expect("select")
                                    EditorInner(ui, it.nextToken(), it.nextToken(), meta, tag, change)
                                }
                                else -> {
                                    it.expect("to")
                                    val change = it.nextToken()
                                    it.expect("select")
                                    EditorInner(ui, it.nextToken(), it.nextToken(), meta, change)
                                }
                            }
                        }
                        else -> error("unknown ui")
                    }
                }
                /**
                 * editor target in list page [page] select [questID] [innerID]
                 * editor target in edit [meta] select [questID] [innerID] [targetID]
                 */
                case("target") {
                    it.mark()
                    it.expect("in")
                    when (val ui = TargetUi.valueOf(it.nextToken().uppercase())) {
                        TargetUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            it.expect("select")
                            EditorTarget(ui, it.nextToken(), it.nextToken(), page = page)
                        }
                        TargetUi.EDIT -> {
                            when (val meta = it.nextToken()) {
                                "condition" -> {
                                    it.expect("page")
                                    val page = it.nextInt()
                                    it.expect("select")
                                    EditorTarget(ui, it.nextToken(), it.nextToken(), it.nextToken(), meta, page = page)
                                }
                                else -> {
                                    it.expect("select")
                                    EditorTarget(ui, it.nextToken(), it.nextToken(), it.nextToken(), meta)
                                }
                            }
                        }
                        else -> error("unknown ui")
                    }
                }
                /**
                 * editor reward in list page [page] select [questID] [innerID]
                 * editor reward in edit page [page] select [questID] [innerID] [rewardID]
                 * editor reward in del [index] select [questID] [innerID] [rewardID]
                 */
                case("reward") {
                    it.mark()
                    it.expect("in")
                    when (val ui = RewardUi.valueOf(it.nextToken().uppercase())) {
                        RewardUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            it.expect("select")
                            EditorReward(ui, it.nextToken(), it.nextToken(), page = page)
                        }
                        RewardUi.EDIT -> {
                            it.expect("page")
                            val page = it.nextInt()
                            it.expect("select")
                            EditorReward(ui, it.nextToken(), it.nextToken(), it.nextToken(), page = page)
                        }
                        RewardUi.DEL -> {
                            val index = it.nextInt()
                            it.expect("select")
                            EditorReward(ui, it.nextToken(), it.nextToken(), it.nextToken(), index = index)
                        }
                        else -> error("unknown ui")
                    }
                }
                /**
                 * editor fail in list page [page] select [questID] [innerID]
                 * editor fail in del [index] select [questID] [innerID]
                 */
                case("fail") {
                    it.mark()
                    it.expect("in")
                    when (val ui = ListUi.valueOf(it.nextToken().uppercase())) {
                        ListUi.LIST -> {
                            it.expect("page")
                            val page = it.nextInt()
                            it.expect("select")
                            EditorInnerFail(ui, it.nextToken(), it.nextToken(), page=page)
                        }
                        ListUi.DEL -> {
                            val index = it.nextToken()
                            it.expect("select")
                            EditorInnerFail(ui, it.nextToken(), it.nextToken(), change = index)
                        }
                        else -> error("unknown ui")
                    }
                }
                case("dialog") {
                    EditorDialog()
                }
            }
        }
    }
}