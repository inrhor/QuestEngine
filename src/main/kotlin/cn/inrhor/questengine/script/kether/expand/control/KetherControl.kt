package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.quest.manager.QuestManager
import cn.inrhor.questengine.common.database.data.DataStorage
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControl(val type: Type, var time: Int, val questID: String, val mainQuestID: String, val priority: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        time = if (type == Type.MINUTE) time*1200 else time*20
        WaitRun.run(player.cast(), time, questID, mainQuestID, priority)
        return CompletableFuture.completedFuture(null)
    }

    object WaitRun {
        fun run(player: Player, time: Int, questID: String, mainQuestID: String, priority: String) {
            val pData = DataStorage.getPlayerData(player)
            val controlID = QuestManager.generateControlID(questID, mainQuestID, priority)
            val cData = pData.controlData
            if (cData.highestControls.containsKey(controlID)) {
                highest(controlID, cData, time)
            }else {
                normal(controlID, cData, time)
            }
        }

        fun highest(controlID: String, controlData: ControlData, time: Int) {
            val hControl = controlData.highestControls[controlID]?: return
            hControl.waitTime = time
        }

        fun normal(controlID: String, controlData: ControlData, time: Int) {
            val nControl = controlData.controls[controlID]?: return
            nControl.waitTime = time
        }
    }

    enum class Type {
        SECOND, MINUTE
    }

    /*
     * wait type [time] to [questID] [mainQuestID] the [priority]
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
            it.expect("to")
            val questID = it.nextToken()
            val innerID = it.nextToken()
            it.mark()
            it.expect("the")
            KetherControl(timeUnit, time, questID, innerID, it.nextToken())
        }
    }

}