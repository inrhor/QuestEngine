package cn.inrhor.questengine.utlis.component

import taboolib.module.chat.ComponentText

interface NewPropertyValue {

    /** 文本 */
    class Text(val text: String) : NewPropertyValue {

        override fun toString(): String {
            return text
        }
    }

    /** 链接 */
    class Link(val name: String) : NewPropertyValue {

        /** 获取链接对应的值 */
        fun getValue(transfer: NewTextTransfer): ComponentText {
            return transfer.component.linkData[name]!!.toBuild(transfer)
        }

        override fun toString(): String {
            return name
        }
    }

}