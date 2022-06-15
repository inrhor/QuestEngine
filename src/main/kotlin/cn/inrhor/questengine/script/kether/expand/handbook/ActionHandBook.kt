package cn.inrhor.questengine.script.kether.expand.handbook

import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.questNoteBuild
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.questSortBuild
import cn.inrhor.questengine.common.quest.ui.QuestBookBuildManager.targetNodeBuild
import cn.inrhor.questengine.script.kether.player
import taboolib.module.kether.KetherParser
import taboolib.module.kether.actionNow
import taboolib.module.kether.scriptParser
import taboolib.module.kether.switch

object ActionHandBook {

    @KetherParser(["handbook"], namespace = "QuestEngine", shared = true)
    fun parser() = scriptParser {
        it.switch {
            case("sort") {
                val sort = it.nextToken()
                actionNow {
                    if (QuestBookBuildManager.sortQuest.containsKey(sort)) {
                        player().questSortBuild(sort)
                    }
                }
            }
            case("info") {
                val id = it.nextToken()
                actionNow {
                    player().questNoteBuild(id)
                }
            }
            case("target") {
                val id = it.nextToken()
                actionNow {
                    player().targetNodeBuild(id)
                }
            }
        }
    }

}