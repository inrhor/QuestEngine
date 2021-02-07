package cn.inrhor.questengine.common.dialog.animation

/**
 * @param contentList 该动画标签的所有动态文字内容
 * @param type 文字类型
 * @param delay 延迟播放
 * @param speed 打字速度
 * @param tagIndex 位于标签第几位
 * @param textFrame 已经显示了动态文字 contentList 中的几行
 * @param writeSpeed 为了在动态过程达到speed将显示下一行动态文字
 */
class TagText(var contentList: MutableList<String>, var type: String, val delay: Int,
              var speed: Int, var timeLong: Int, var tagIndex: Int,
              var textFrame: Int, var writeSpeed: Int) {
    constructor(type: String, delay: Int, tagIndex: Int):
            this(mutableListOf(), type, delay, 0, 0, tagIndex, 0, 0)
    constructor(contentList: MutableList<String>, type: String, delay: Int, tagIndex: Int):
            this(contentList, type, delay, 0, 0, tagIndex, 0, 0)
}