package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.TagEvent
import cn.inrhor.questengine.api.manager.TagsManager.addTag
import cn.inrhor.questengine.api.manager.TagsManager.clearTag
import cn.inrhor.questengine.api.manager.TagsManager.removeTag
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object TagListener {

    @SubscribeEvent
    fun onAddTag(ev: TagEvent.AddTag) {
        val player = ev.player
        val tag = ev.tag
        player.addTag(tag)
        Database.database.addTag(player, tag)
    }

    @SubscribeEvent
    fun onRemoveTag(ev: TagEvent.RemoveTag) {
        val player = ev.player
        val tag = ev.tag
        player.removeTag(tag)
        Database.database.removeTag(player, tag)
    }

    @SubscribeEvent
    fun onClearTag(ev: TagEvent.ClearTag) {
        val player = ev.player
        player.clearTag()
        Database.database.clearTag(player)
    }

}