package cn.inrhor.questengine.common.dialog

import java.util.regex.Pattern

class TextAnimation(
    val id: String,
    val textContent: MutableList<String>
) {

    private var text: HashMap<Int, MutableList<String>> = LinkedHashMap<Int, MutableList<String>>()

    fun init() {
        textContent.forEach { a ->
            // 对每一行

            // 分割 取 独立内容
            val pContent = Pattern.compile("<(.*)>")
            val content = pContent.matcher(a)
            while (content.find()) {

                // 分割 取 内容的属性
                val pAttribute = Pattern.compile("\\[(.*)]")
                val attribute = pAttribute.matcher(content.group(1))
                val delay = attribute.group(2)
                when (attribute.group(1)) {
                    "normal" -> {

                    }
                    "flash" -> {

                    }
                    "write" -> {
                        val speed = attribute.group(3)
                    }
                }

            }
        }
    }
}