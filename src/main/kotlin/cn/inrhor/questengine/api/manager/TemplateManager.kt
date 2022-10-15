package cn.inrhor.questengine.api.manager

import cn.inrhor.questengine.api.template.TemplateFrame

object TemplateManager {

    private val templateMap = mutableMapOf<String, TemplateFrame>()

    fun TemplateFrame.register(key: String) {
        templateMap[key] = this
    }

    fun String.getTemplate(): TemplateFrame? = templateMap[this]

    fun clear() = templateMap.clear()
}