package cn.inrhor.questengine.api.manager

import cn.inrhor.questengine.api.event.TagEvent
import cn.inrhor.questengine.api.manager.DataManager.tagsData
import org.bukkit.entity.Player

object TagsManager {

    /**
     * 添加标签
     */
    fun Player.addTag(tag: String) {
        TagEvent.Add(this, tag).call()
    }

    /**
     * 移除标签
     */
    fun Player.removeTag(tag: String) {
        TagEvent.Remove(this, tag).call()
    }

    /**
     * 清除标签
     */
    fun Player.clearTag() {
        TagEvent.Clear(this).call()
    }

    /**
     * 是否拥有标签
     */
    fun Player.hasTag(tag: String): Boolean {
        return tagsData().tags.contains(tag)
    }

    /**
     * 获取标签列表
     */
    fun Player.tags(): MutableSet<String> {
        return tagsData().tags
    }

}