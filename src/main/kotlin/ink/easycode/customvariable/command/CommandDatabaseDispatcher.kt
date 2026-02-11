package ink.easycode.customvariable.command

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning

object CommandDatabaseDispatcher {

    fun dispatch(sender: ProxyCommandSender, taskName: String, block: () -> Unit) {
        val task = {
            runCatching(block).onFailure { ex ->
                warning("[CustomVariable] Async command database task failed ($taskName): ${ex.message}")
                CustomVariableLang.send(sender, LangKeys.ERROR_INTERNAL)
            }
        }
        if (isPrimaryThread) {
            submitAsync { task() }
        } else {
            task()
        }
    }
}
