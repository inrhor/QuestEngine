package cn.inrhor.questengine.utlis

object ListUtil {

    fun pathList(count: Int): List<Int> {
        val a = mutableListOf<Int>()

        // 将列表元素数量添加到a列表
        a.add(0)

        // 通过循环计算并将结果添加到a列表中
        for (i in 1..count/2) {
            a.add(i)
            a.add(-i)
        }

        // 如果列表元素数量是奇数，则需要添加最后一个正数或负数
        if (count % 2 != 0) {
            if (count > 0) {
                a.add((count / 2) + 1)
            } else {
                a.add(-(count / 2) - 1)
            }
        }

        return a
    }


}