package cn.inrhor.questengine.common.hook.invero

import cc.trixey.invero.core.Context
import cc.trixey.invero.core.geneartor.ContextGenerator

class GroupDoingGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.groupGenerate(context, UiType.GROUP_DOING)
    }

}

class GroupCompleteGenerator: ContextGenerator() {

    override fun generate(context: Context) {
        generated = InvGenerator.groupGenerate(context, UiType.GROUP_COMPLETE)
    }

}