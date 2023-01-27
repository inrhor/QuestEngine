package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.api.manager.DataManager.doingTargets
import cn.inrhor.questengine.script.kether.runEval

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent

object TPlayerChat: TargetExtend<AsyncPlayerChatEvent>() {

    override val name = "player chat"

    override val isAsync = true

    init {
        event = AsyncPlayerChatEvent::class
        tasker{
            player.doingTargets(name).forEach {
                val t = it.getTargetFrame()?: return@forEach
                if (targetTrigger(player, "message", message, t)) {
                    Schedule.isNumber(player, "number", it)
                }
            }
            player
        }
    }

    /**
     * 匹配文字
     *
     * @param tag 键
     * @param content 需要的匹配内容
     */
    fun targetTrigger(player: Player, tag: String, content: String, target: TargetFrame): Boolean {
        val condition = target.nodeMeta(tag)
        return runEval(player, "strMatch type ${condition[0]} '$content'")
    }

}