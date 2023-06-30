package cn.inrhor.questengine.utlis.component

import taboolib.module.chat.colored
import taboolib.module.chat.uncolored

class NewTextTransfer(val component: NewSimpleComponent) {

    internal val transforms = arrayListOf<(String) -> String>()

    /** 转换文本 */
    internal operator fun invoke(text: Any?): String {
        var t = text.toString()
        transforms.forEach { t = it(t) }
        return t
    }

    /** 添加转换器 */
    fun transform(block: (String) -> String): NewTextTransfer {
        transforms += block
        return this
    }

    /** 上色 */
    fun colored(): NewTextTransfer {
        return transform { it.colored() }
    }

    /** 去色 */
    fun uncolored(): NewTextTransfer {
        return transform { it.uncolored() }
    }
}