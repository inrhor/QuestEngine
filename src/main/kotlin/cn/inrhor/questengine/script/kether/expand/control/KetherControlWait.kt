package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.common.database.data.ControlData
import cn.inrhor.questengine.common.database.data.DataStorage
import cn.inrhor.questengine.common.quest.manager.ControlManager
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.module.kether.*
import taboolib.module.kether.scriptParser
import java.util.concurrent.CompletableFuture

class KetherControlWait(val type: Type, val time: Int, val questID: String, val innerQuestID: String, val id: String): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.script().sender as? ProxyPlayer ?: error("unknown player")
        val wait = if (type == Type.MINUTE) time*1200 else time*20
        WaitRun.run(player.cast(), wait, questID, innerQuestID, id)
        return CompletableFuture.completedFuture(null)
    }

    object WaitRun {
        fun run(player: Player, time: Int, questID: String, innerQuestID: String, id: String) {
            val pData = DataStorage.getPlayerData(player)
            val controlID = ControlManager.generateControlID(questID, innerQuestID, id)
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
     * wait unit/timeunit [unit] [time] to [questID] [innerQuestID] the [priority]
     */
    internal object Parser {

        @KetherParser(["waitTime"], namespace = "QuestEngine")
        fun parser() = scriptParser {
            val timeUnit = try {
                when (val type = it.nextToken()) {
                    "s", "second" -> Type.SECOND
                    "m", "minute" -> Type.MINUTE
                    else -> throw KetherError.CUSTOM.create("未知时间类型: $type")
                }
            } catch (ignored: Exception) {
                Type.SECOND
            }
            val time = it.nextInt()
            it.mark()
            it.expect("to")
            val questID = it.nextToken()
            val innerID = it.nextToken()
            KetherControlWait(timeUnit, time, questID, innerID, it.nextToken())
        }
    }

}