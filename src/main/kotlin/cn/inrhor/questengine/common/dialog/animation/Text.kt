package cn.inrhor.questengine.common.dialog.animation

/**
 * @param contentList 所有动画标签内容
 * @param type 文字类型
 * @param delay 延迟播放
 * @param speed 打字速度
 */
class Text(var contentList: MutableList<String>, var type: String, val delay: Int, var speed: Int, var timeLong: Int) {
    constructor(type: String, delay: Int):
            this(mutableListOf(), type, delay, 0, 0)
    constructor(contentList: MutableList<String>, type: String, delay: Int):
            this(contentList, type, delay, 0, 0)
}