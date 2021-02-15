package cn.inrhor.questengine.common.dialog.location

import org.bukkit.Location

class LocationTool {
    /**
     * 固定跟踪
     *
     * 偏移量 offset
     * -90 实体的左边
     * 90  实体的右边
     * 180 实体的后面
     * 想要在前面，直接负数乘法完事了，因为-180也是在后面
     *
     * @param ownLoc
     * @param offset
     * @param multiply 乘 越大越远之类
     * @param height
     * @return
     */
    fun getFixedLoc(ownLoc: Location, offset: Float, multiply: Double, height: Double): Location? {
        // 复制本体位置
        val fixedLoc = ownLoc.clone()
        fixedLoc.yaw = ownLoc.yaw + offset
        val vectorAdd = fixedLoc.direction.normalize().multiply(multiply)
        val fixed = fixedLoc.add(vectorAdd)
        fixed.add(0.0, height, 0.0)
        return fixed
    }

    fun getFixedLoc(ownLoc: Location, fixedLoc: FixedLocation): Location {
        return getFixedLoc(ownLoc, fixedLoc.offset, fixedLoc.multiply, fixedLoc.height)!!
    }
}