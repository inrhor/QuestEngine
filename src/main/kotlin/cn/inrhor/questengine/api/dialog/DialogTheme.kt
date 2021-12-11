package cn.inrhor.questengine.api.dialog

/**
 * 对话主题接口
 */
interface  DialogTheme {

    /**
     * 播放对话
     */
    fun play()

}

/**
 * 对话播放接口
 */
interface DialogPlay {
    var delay: Long
    var speed: Long
}

/**
 * 对话文本接口
 */
interface TextPlay: DialogPlay {
    var text: String

    override var delay: Long
        get() = 0L
        set(value) {}

    override var speed: Long
        get() = 0L
        set(value) {}
}