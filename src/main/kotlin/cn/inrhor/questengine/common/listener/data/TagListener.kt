package cn.inrhor.questengine.common.listener.data

import cn.inrhor.questengine.api.event.TagEvent
import cn.inrhor.questengine.api.manager.DataManager.tagsData
import cn.inrhor.questengine.common.database.Database
import taboolib.common.platform.event.SubscribeEvent

object TagListener {

    @SubscribeEvent
    fun onAddTag(ev: TagEvent.Add) {
        val player = ev.player
        val tag = ev.tag
        player.tagsData().tags.add(tag)
        Database.database.addTag(player, tag)
    }

    @SubscribeEvent
    fun onRemoveTag(ev: TagEvent.Remove) {
        val player = ev.player
        val tag = ev.tag
        player.tagsData().tags.remove(tag)
        Database.database.removeTag(player, tag)
    }

    @SubscribeEvent
    fun onClearTag(ev: TagEvent.Clear) {
        val player = ev.player
        player.tagsData().tags.clear()
        Database.database.clearTag(player)
    }

}