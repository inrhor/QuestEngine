package cn.inrhor.questengine.common.quest.target.bukkit

import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.TriggerUtils.triggerTarget

import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * 已重写
 */
object TPlayerChat: TargetExtend<AsyncPlayerChatEvent>() {

    override val name = "player chat"

    override val isAsync = true

    init {
        event = AsyncPlayerChatEvent::class
        tasker{
            player.triggerTarget(name) { _, pass ->
                val msg = pass.content
                msg.isEmpty() || msg.any { message.contains(it) }
            }
        }
    }

}