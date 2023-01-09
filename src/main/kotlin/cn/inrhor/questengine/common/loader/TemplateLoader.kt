package cn.inrhor.questengine.common.loader

import cn.inrhor.questengine.api.manager.TemplateManager.register
import cn.inrhor.questengine.api.template.TemplateFrame
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject

object TemplateLoader {

    fun file() {
        val folder = FileUtil.getFile("space/template/", "TEMPLATE-NO_FILES", true)

        FileUtil.getFileList(folder).forEach{
            val yaml = Configuration.loadFromFile(it)
            yaml.getConfigurationSection("")?.getKeys(false)?.forEach { k ->
                yaml.getObject<TemplateFrame>(k, false).register(k)
            }
        }
    }

}