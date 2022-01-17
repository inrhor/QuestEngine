package cn.inrhor.questengine.command

import cn.inrhor.questengine.command.handbook.*
import taboolib.common.platform.command.*

object HandbookCommand {

    @CommandBody
    val home = HandbookHome.home

    @CommandBody
    val sort = HandbookSort.sort

    @CommandBody
    val info = HandbookInfo.info

    @CommandBody
    val innerList = HandbookInnerList.innerList

    @CommandBody
    val inner = HandbookInner.inner

    @CommandBody
    val target = HandbookTarget.target

}
