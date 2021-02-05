package cn.inrhor.questengine.common.dialog.animation

/**
 * @param contentList 所有动画标签内容
 * @param delay 延迟播放
 * @param speed 打字速度
 */
class Text(var contentList: MutableList<String>, val delay: Int, var speed: Int) {
    constructor(delay: Int):
            this(mutableListOf(), delay, 0)
    constructor(contentList: MutableList<String>, delay: Int):
            this(contentList, delay, 0)
}