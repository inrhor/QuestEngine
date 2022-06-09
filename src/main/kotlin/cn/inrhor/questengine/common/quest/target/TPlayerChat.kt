package cn.inrhor.questengine.common.quest.target

import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.TargetExtend
import cn.inrhor.questengine.api.target.util.Schedule
import cn.inrhor.questengine.common.database.data.doingTargets
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
                if (targetTrigger(player, name, "message", message, it.getTargetFrame())) {
                    Schedule.isNumber(player, name, "number", it)
                }
            }
            player
        }
    }

    /**
     * 匹配文字
     *
     * @param name 事件名称
     * @param tag 键
     * @param content 需要的匹配内容
     */
    fun targetTrigger(player: Player, name: String, tag: String, content: String, target: TargetFrame): Boolean {
        val condition = target.nodeMeta(tag)?: return false
        return runEval(player, "strMatch type $condition *'$content'")
    }

}