package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.tags.TagsAdd
import cn.inrhor.questengine.command.tags.TagsList
import cn.inrhor.questengine.command.tags.TagsRemove
import taboolib.common.platform.command.*

object TagsCommand {

    @CommandBody
    val list = TagsList.list

    @CommandBody
    val add = TagsAdd.add

    @CommandBody
    val remove = TagsRemove.remove

}
