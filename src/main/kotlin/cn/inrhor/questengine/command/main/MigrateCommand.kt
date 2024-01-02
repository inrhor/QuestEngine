package cn.inrhor.questengine.command.main

import cn.inrhor.questengine.common.database.type.DatabaseType
import cn.inrhor.questengine.common.migrate.MigrateDatabase
import cn.inrhor.questengine.common.migrate.MigrateQuest
import cn.inrhor.questengine.utlis.file.FileUtil
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.subCommand
import taboolib.module.configuration.Configuration

object MigrateCommand {

    val migrate = subCommand {
        literal("target") {
            execute<ProxyCommandSender> { _, _, _ ->
                MigrateQuest().task()
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
        // 迁移数据
        literal("database") {
            // 旧数据
            literal("old") {
                // 旧数据 -> 新数据
                execute<ProxyCommandSender> { sender, _, _ ->
                    MigrateDatabase().oldToNew(sender)
                }
            }
            // 转换
            literal("from") {
                dynamic("type") {
                    suggestion<ProxyCommandSender> { _, _ ->
                        listOf("LOCAL", "MYSQL")
                    }
                    execute<ProxyCommandSender> { sender, context, _ ->
                        MigrateDatabase().fromToDatabase(sender, DatabaseType.valueOf(context["type"]))
                    }
                }
            }
        }
    }

}