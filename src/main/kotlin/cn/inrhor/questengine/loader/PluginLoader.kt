package cn.inrhor.questengine.loader

import cn.inrhor.questengine.common.dialog.DialogManager
import cn.inrhor.questengine.utlis.public.UseString

class PluginLoader {

    fun init() {
        UpdateYaml().run(UseString.getLang())
        InfoSend().logoSend()

        DialogManager().loadDialog()
    }

}