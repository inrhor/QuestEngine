package cn.inrhor.questengine.common.dialog.animation.text.type

class TextWrite(val delay: Int, val speedWrite: Int, val text: String, val type: Type, val sendChat: Boolean = false) {

    enum class Type {
        TEXTWRITE, EMPTYWRITE
    }

}