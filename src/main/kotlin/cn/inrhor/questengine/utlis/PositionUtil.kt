package cn.inrhor.questengine.utlis

object PositionUtil {

    /**
     * @return 旋转结果
     */
    fun Float.rotate(): Float {
        return this * 150.0F / 360.0F
    }

}