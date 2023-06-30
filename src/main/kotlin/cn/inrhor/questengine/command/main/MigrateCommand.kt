package cn.inrhor.questengine.command.main

import cn.inrhor.questengine.common.quest.MigrateMode
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.module.configuration.Configuration

object MigrateCommand {

    val migrate = subCommand {
        literal("target") {
            execute<ProxyCommandSender> { _, _, _ ->
                MigrateMode().task()
            }
        }
        literal("nav") {
            execute<ProxyCommandSender> { _, _, _ ->
                val a = FileUtil.getFile("data")
                FileUtil.getFileList(a).forEach {
                    Configuration.loadFromFile(it)["nav"] = null
                }
            }
        }
    }

}