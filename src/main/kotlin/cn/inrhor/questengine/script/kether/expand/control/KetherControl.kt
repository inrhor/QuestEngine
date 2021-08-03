package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControl(val type: Type, var time: Int, val questID: String, val mainQuestID: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        time = if (type == Type.MINUTE) time*1200 else time*20
        WaitRun.run(player.cast(), time, questID, mainQuestID)
        return CompletableFuture.completedFuture(null)
    }

    object WaitRun {
        fun run(player: Player, time: Int, questID: String, mainQuestID: String) {
            val pData = DataStorage.getPlayerData(player)
            val cMap = pData.controlList
            val id = QuestManager.generateControlID(questID, mainQuestID)
            if (cMap.containsKey(id)) {
                val cData = cMap[id]?: return
                cData.waitTime = time
            }
        }
    }

    enum class Type {
        SECOND, MINUTE
    }

    /*
     * wait type [time] to [questID] [mainQuestID]
     */
    internal object Parser {

        @KetherParser(["wait"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            it.mark()
            val timeUnit = try {
                it.expects("unit", "timeunit")
                when (val type = it.nextToken()) {
                    "s", "second" -> Type.SECOND
                    "minute" -> Type.MINUTE
                    else -> throw KetherError.CUSTOM.create("未知时间类型: $type")
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.SECOND
            }
            val time = it.nextInt()
            it.mark()
            it.expects("to")
            KetherControl(timeUnit, time, it.nextToken(), it.nextToken())
        }
    }

}